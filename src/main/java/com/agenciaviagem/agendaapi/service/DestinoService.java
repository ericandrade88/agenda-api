package com.agenciaviagem.agendaapi.service;

import com.agenciaviagem.agendaapi.dto.DestinoRequest;
import com.agenciaviagem.agendaapi.exception.ResourceNotFoundException;
import com.agenciaviagem.agendaapi.model.Destino;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Camada de servico responsavel por toda a logica de negocio relacionada
 * a destinos de viagem: cadastro, listagem, pesquisa, avaliacao e exclusao.
 *
 * Conforme o escopo do desafio, os dados sao mantidos em memoria
 * (sem integracao com banco de dados).
 */
@Service
public class DestinoService {

    private final Map<Long, Destino> destinos = new ConcurrentHashMap<>();
    private final AtomicLong sequenciaId = new AtomicLong(1);

    /**
     * Cadastra um novo destino de viagem.
     */
    public Destino cadastrar(DestinoRequest request) {
        Long id = sequenciaId.getAndIncrement();
        Destino destino = new Destino(
                id,
                request.getNome(),
                request.getLocalizacao(),
                request.getDescricao(),
                request.getPrecoPacote()
        );
        destinos.put(id, destino);
        return destino;
    }

    /**
     * Retorna todos os destinos cadastrados.
     */
    public List<Destino> listarTodos() {
        return List.copyOf(destinos.values());
    }

    /**
     * Busca um destino especifico pelo id, lancando excecao caso nao exista.
     */
    public Destino buscarPorId(Long id) {
        Destino destino = destinos.get(id);
        if (destino == null) {
            throw new ResourceNotFoundException("Destino nao encontrado com id: " + id);
        }
        return destino;
    }

    /**
     * Pesquisa destinos por nome e/ou localizacao (busca parcial, case-insensitive).
     * Os dois parametros sao opcionais; quando ambos sao informados, os dois
     * criterios devem ser atendidos.
     */
    public List<Destino> pesquisar(String nome, String localizacao) {
        return destinos.values().stream()
                .filter(destino -> nome == null || nome.isBlank()
                        || destino.getNome().toLowerCase().contains(nome.toLowerCase()))
                .filter(destino -> localizacao == null || localizacao.isBlank()
                        || destino.getLocalizacao().toLowerCase().contains(localizacao.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Atualiza os dados cadastrais de um destino existente.
     */
    public Destino atualizar(Long id, DestinoRequest request) {
        Destino destino = buscarPorId(id);
        destino.setNome(request.getNome());
        destino.setLocalizacao(request.getLocalizacao());
        destino.setDescricao(request.getDescricao());
        destino.setPrecoPacote(request.getPrecoPacote());
        return destino;
    }

    /**
     * Registra uma nova avaliacao (nota de 1 a 10) para o destino e
     * recalcula a media com base nas avaliacoes ja existentes.
     */
    public Destino avaliar(Long id, Integer nota) {
        if (nota == null || nota < 1 || nota > 10) {
            throw new IllegalArgumentException("A nota deve estar entre 1 e 10");
        }

        Destino destino = buscarPorId(id);

        double somaAtual = destino.getNotaMedia() * destino.getQuantidadeAvaliacoes();
        int novaQuantidade = destino.getQuantidadeAvaliacoes() + 1;
        double novaMedia = (somaAtual + nota) / novaQuantidade;

        destino.setQuantidadeAvaliacoes(novaQuantidade);
        destino.setNotaMedia(arredondar(novaMedia));

        return destino;
    }

    /**
     * Exclui um destino de viagem pelo id.
     */
    public void excluir(Long id) {
        buscarPorId(id);
        destinos.remove(id);
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
