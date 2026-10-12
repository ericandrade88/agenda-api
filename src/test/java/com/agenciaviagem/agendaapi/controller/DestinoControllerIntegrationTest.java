package com.agenciaviagem.agendaapi.controller;

import com.agenciaviagem.agendaapi.dto.AvaliacaoRequest;
import com.agenciaviagem.agendaapi.dto.DestinoRequest;
import com.agenciaviagem.agendaapi.dto.ReservaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Fluxo completo da API com banco H2 (perfil "test") e autenticacao HTTP Basic.
 * ADMIN gerencia destinos; CLIENTE avalia e reserva.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DestinoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveGerenciarFluxoCompletoDoDestino() throws Exception {
        DestinoRequest destinoRequest = new DestinoRequest();
        destinoRequest.setNome("Salvador");
        destinoRequest.setLocalizacao("Bahia");
        destinoRequest.setDescricao("Praias e cultura");
        destinoRequest.setPrecoPacote(1800.0);

        String destinoJson = objectMapper.writeValueAsString(destinoRequest);

        String corpoCriado = mockMvc.perform(post("/api/destinos")
                        .with(httpBasic("admin@agencia.com", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(destinoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Salvador"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(corpoCriado).get("id").asLong();

        mockMvc.perform(get("/api/destinos").with(httpBasic("cliente@agencia.com", "cliente123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome", hasItem("Salvador")));

        mockMvc.perform(get("/api/destinos/pesquisa")
                        .with(httpBasic("parceiro@agencia.com", "parceiro123"))
                        .param("nome", "salv").param("localizacao", "bahia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].nome", hasItem("Salvador")));

        mockMvc.perform(get("/api/destinos/{id}", id).with(httpBasic("cliente@agencia.com", "cliente123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        destinoRequest.setNome("Salvador Atualizado");
        destinoRequest.setDescricao("Praias, cultura e museus");
        destinoRequest.setPrecoPacote(2100.0);

        mockMvc.perform(put("/api/destinos/{id}", id)
                        .with(httpBasic("admin@agencia.com", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destinoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Salvador Atualizado"));

        AvaliacaoRequest avaliacaoRequest = new AvaliacaoRequest();
        avaliacaoRequest.setNota(8);

        mockMvc.perform(patch("/api/destinos/{id}/avaliacoes", id)
                        .with(httpBasic("cliente@agencia.com", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avaliacaoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notaMedia").value(8.0));

        AvaliacaoRequest segundaAvaliacao = new AvaliacaoRequest();
        segundaAvaliacao.setNota(10);

        mockMvc.perform(patch("/api/destinos/{id}/avaliacoes", id)
                        .with(httpBasic("cliente@agencia.com", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundaAvaliacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notaMedia").value(9.0));

        ReservaRequest reservaRequest = new ReservaRequest();
        reservaRequest.setNomeCliente("Maria");
        reservaRequest.setNumeroPessoas(2);

        mockMvc.perform(post("/api/destinos/{id}/reservas", id)
                        .with(httpBasic("cliente@agencia.com", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeCliente").value("Maria"))
                .andExpect(jsonPath("$.destinoId").value(id));

        mockMvc.perform(get("/api/destinos/{id}/reservas", id)
                        .with(httpBasic("admin@agencia.com", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeCliente").value("Maria"));

        mockMvc.perform(delete("/api/destinos/{id}", id).with(httpBasic("admin@agencia.com", "admin123")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/destinos/{id}", id).with(httpBasic("admin@agencia.com", "admin123")))
                .andExpect(status().isNotFound());
    }
}
