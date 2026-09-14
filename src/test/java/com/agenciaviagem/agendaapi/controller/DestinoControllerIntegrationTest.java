package com.agenciaviagem.agendaapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.agenciaviagem.agendaapi.dto.AvaliacaoRequest;
import com.agenciaviagem.agendaapi.dto.DestinoRequest;
import com.agenciaviagem.agendaapi.dto.ReservaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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

        String destinoId = mockMvc.perform(post("/api/destinos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(destinoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Salvador"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(destinoId).get("id").asLong();

        mockMvc.perform(get("/api/destinos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Salvador"));

        mockMvc.perform(get("/api/destinos/pesquisa").param("nome", "salv").param("localizacao", "bahia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Salvador"));

        mockMvc.perform(get("/api/destinos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        destinoRequest.setNome("Salvador Atualizado");
        destinoRequest.setDescricao("Praias, cultura e museus");
        destinoRequest.setPrecoPacote(2100.0);

        mockMvc.perform(put("/api/destinos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(destinoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Salvador Atualizado"));

        AvaliacaoRequest avaliacaoRequest = new AvaliacaoRequest();
        avaliacaoRequest.setNota(8);

        mockMvc.perform(patch("/api/destinos/{id}/avaliacoes", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(avaliacaoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notaMedia").value(8.0));

        AvaliacaoRequest segundaAvaliacao = new AvaliacaoRequest();
        segundaAvaliacao.setNota(10);

        mockMvc.perform(patch("/api/destinos/{id}/avaliacoes", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundaAvaliacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notaMedia").value(9.0));

        ReservaRequest reservaRequest = new ReservaRequest();
        reservaRequest.setNomeCliente("Maria");
        reservaRequest.setNumeroPessoas(2);

        mockMvc.perform(post("/api/destinos/{id}/reservas", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservaRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeCliente").value("Maria"));

        mockMvc.perform(get("/api/destinos/{id}/reservas", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomeCliente").value("Maria"));

        mockMvc.perform(delete("/api/destinos/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/destinos/{id}", id))
                .andExpect(status().isNotFound());
    }
}
