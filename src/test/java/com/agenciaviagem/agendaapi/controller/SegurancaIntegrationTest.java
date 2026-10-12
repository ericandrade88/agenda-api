package com.agenciaviagem.agendaapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica a autenticacao (usuarios do banco) e a autorizacao por perfil.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SegurancaIntegrationTest {

    private static final String DESTINO_JSON = """
            {"nome":"Gramado","localizacao":"Rio Grande do Sul","descricao":"Serra gaucha","precoPacote":900.0}
            """;
    private static final String AVALIACAO_JSON = "{\"nota\":9}";
    private static final String RESERVA_JSON = "{\"nomeCliente\":\"Ana\",\"numeroPessoas\":2}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void semCredenciais_retorna401() throws Exception {
        mockMvc.perform(get("/api/destinos")).andExpect(status().isUnauthorized());
    }

    @Test
    void senhaErrada_retorna401() throws Exception {
        mockMvc.perform(get("/api/destinos").with(httpBasic("admin@agencia.com", "errada")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioInexistente_retorna401() throws Exception {
        mockMvc.perform(get("/api/destinos").with(httpBasic("ninguem@agencia.com", "x")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void parceiro_consultaMasNaoAltera() throws Exception {
        mockMvc.perform(get("/api/destinos").with(httpBasic("parceiro@agencia.com", "parceiro123")))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/destinos").with(httpBasic("parceiro@agencia.com", "parceiro123"))
                        .contentType(MediaType.APPLICATION_JSON).content(DESTINO_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/destinos/1/avaliacoes").with(httpBasic("parceiro@agencia.com", "parceiro123"))
                        .contentType(MediaType.APPLICATION_JSON).content(AVALIACAO_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/destinos/1/reservas").with(httpBasic("parceiro@agencia.com", "parceiro123"))
                        .contentType(MediaType.APPLICATION_JSON).content(RESERVA_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void cliente_naoCadastraNemExcluiDestino() throws Exception {
        mockMvc.perform(post("/api/destinos").with(httpBasic("cliente@agencia.com", "cliente123"))
                        .contentType(MediaType.APPLICATION_JSON).content(DESTINO_JSON))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/destinos/1").with(httpBasic("cliente@agencia.com", "cliente123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void cliente_naoListaReservasDoDestino() throws Exception {
        mockMvc.perform(get("/api/destinos/1/reservas").with(httpBasic("cliente@agencia.com", "cliente123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void cliente_naoAcessaGestaoDeUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios").with(httpBasic("cliente@agencia.com", "cliente123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_acessaGestaoDeUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios").with(httpBasic("admin@agencia.com", "admin123")))
                .andExpect(status().isOk());
    }

    @Test
    void registroPublico_criaClienteQuePodeAutenticar() throws Exception {
        mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Maria\",\"email\":\"maria@teste.com\",\"senha\":\"123456\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/auth/me").with(httpBasic("maria@teste.com", "123456")))
                .andExpect(status().isOk());

        // cliente recem-criado nao pode cadastrar destinos
        mockMvc.perform(post("/api/destinos").with(httpBasic("maria@teste.com", "123456"))
                        .contentType(MediaType.APPLICATION_JSON).content(DESTINO_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void registroPublico_comEmailRepetido_retorna400() throws Exception {
        String corpo = "{\"nome\":\"Joao\",\"email\":\"joao@teste.com\",\"senha\":\"123456\"}";
        mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/auth/registro").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest());
    }
}
