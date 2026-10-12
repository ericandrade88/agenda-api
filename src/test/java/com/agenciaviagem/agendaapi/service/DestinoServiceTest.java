package com.agenciaviagem.agendaapi.service;

import com.agenciaviagem.agendaapi.dto.DestinoRequest;
import com.agenciaviagem.agendaapi.exception.ResourceNotFoundException;
import com.agenciaviagem.agendaapi.model.Destino;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa o DestinoService com o Spring Data JPA de verdade (H2 em memoria),
 * sem precisar do PostgreSQL.
 */
@DataJpaTest
@Import(DestinoService.class)
class DestinoServiceTest {

    @Autowired
    private DestinoService destinoService;

    @Test
    void cadastrar_deveSalvarDestino() {
        DestinoRequest request = criarDestinoRequest("Rio de Janeiro", "Brasil", "Cidade maravilhosa", 2500.0);

        Destino destino = destinoService.cadastrar(request);

        assertNotNull(destino);
        assertNotNull(destino.getId());
        assertEquals("Rio de Janeiro", destino.getNome());
        assertEquals("Brasil", destino.getLocalizacao());
        assertEquals(0.0, destino.getNotaMedia());
        assertEquals(0, destino.getQuantidadeAvaliacoes());
    }

    @Test
    void pesquisar_deveFiltrarPorNomeELocalizacao() {
        destinoService.cadastrar(criarDestinoRequest("Salvador", "Bahia", "Praias", 1800.0));
        destinoService.cadastrar(criarDestinoRequest("Rio de Janeiro", "Rio", "Cristo", 2200.0));
        destinoService.cadastrar(criarDestinoRequest("Florianopolis", "Santa Catarina", "Lagoa", 3100.0));

        List<Destino> resultados = destinoService.pesquisar("rio", "rio");

        assertEquals(1, resultados.size());
        assertEquals("Rio de Janeiro", resultados.get(0).getNome());
    }

    @Test
    void pesquisar_semParametros_deveRetornarTodos() {
        destinoService.cadastrar(criarDestinoRequest("Salvador", "Bahia", "Praias", 1800.0));
        destinoService.cadastrar(criarDestinoRequest("Curitiba", "Parana", "Museus", 1700.0));

        assertEquals(2, destinoService.pesquisar(null, null).size());
    }

    @Test
    void avaliar_deveRecalcularMedia() {
        Destino destino = destinoService.cadastrar(criarDestinoRequest("Porto Seguro", "Bahia", "Praia", 1900.0));

        destinoService.avaliar(destino.getId(), 8);
        destinoService.avaliar(destino.getId(), 10);

        assertEquals(2, destino.getQuantidadeAvaliacoes());
        assertEquals(9.0, destino.getNotaMedia());
    }

    @Test
    void avaliar_comNotaInvalida_deveLancarExcecao() {
        Destino destino = destinoService.cadastrar(criarDestinoRequest("Gramado", "RS", "Serra", 900.0));

        assertThrows(IllegalArgumentException.class, () -> destinoService.avaliar(destino.getId(), 11));
        assertThrows(IllegalArgumentException.class, () -> destinoService.avaliar(destino.getId(), 0));
    }

    @Test
    void excluir_deveRemoverDestino() {
        Destino destino = destinoService.cadastrar(criarDestinoRequest("Curitiba", "Parana", "Museus", 1700.0));

        destinoService.excluir(destino.getId());

        assertThrows(ResourceNotFoundException.class, () -> destinoService.buscarPorId(destino.getId()));
    }

    @Test
    void buscarPorId_quandoDestinoNaoExiste_deveLancarResourceNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> destinoService.buscarPorId(999L));
    }

    private DestinoRequest criarDestinoRequest(String nome, String localizacao, String descricao, Double preco) {
        DestinoRequest request = new DestinoRequest();
        request.setNome(nome);
        request.setLocalizacao(localizacao);
        request.setDescricao(descricao);
        request.setPrecoPacote(preco);
        return request;
    }
}
