package com.agenciaviagem.agendaapi.service;

import com.agenciaviagem.agendaapi.dto.DestinoRequest;
import com.agenciaviagem.agendaapi.exception.ResourceNotFoundException;
import com.agenciaviagem.agendaapi.model.Destino;
import com.agenciaviagem.agendaapi.repository.DestinoRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de servico responsavel por toda a logica de negocio relacionada
 * a destinos de viagem: cadastro, listagem, pesquisa, avaliacao e exclusao.
 *
 * Desafio 2: os dados agora sao persistidos via Spring Data JPA (PostgreSQL).
 */
@Service
@Transactional
public class DestinoService {

    private final DestinoRepository destinoRepository;

    public DestinoService(DestinoRepository destinoRepository) {
        this.destinoRepository = destinoRepository;
    }

    /**
     * Cadastra um novo destino de viagem.
     */
    public Destino cadastrar(DestinoRequest request) {
        Destino destino = new Destino(
                request.getNome(),
                request.getLocalizacao(),
                request.getDescricao(),
                request.getPrecoPacote()
        );
        return destinoRepository.save(destino);
    }

    /**
     * Retorna todos os destinos cadastrados.
     */
    @Transactional(readOnly = true)
    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }

    /**
     * Busca um destino especifico pelo id, lancando excecao caso nao exista.
     */
    @Transactional(readOnly = true)
    public Destino buscarPorId(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destino nao encontrado com id: " + id));
    }

    /**
     * Pesquisa destinos por nome e/ou localizacao (busca parcial, case-insensitive).
     * Os dois parametros sao opcionais; quando ambos sao informados, os dois
     * criterios devem ser atendidos.
     */
    @Transactional(readOnly = true)
    public List<Destino> pesquisar(String nome, String localizacao) {
        Specification<Destino> filtro = Specification.where(null);

        if (nome != null && !nome.isBlank()) {
            filtro = filtro.and(contem("nome", nome));
        }
        if (localizacao != null && !localizacao.isBlank()) {
            filtro = filtro.and(contem("localizacao", localizacao));
        }
        return destinoRepository.findAll(filtro);
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
        return destinoRepository.save(destino);
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

        return destinoRepository.save(destino);
    }

    /**
     * Exclui um destino de viagem pelo id (as reservas do destino sao removidas em cascata).
     */
    public void excluir(Long id) {
        destinoRepository.delete(buscarPorId(id));
    }

    private Specification<Destino> contem(String campo, String valor) {
        String padrao = "%" + valor.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get(campo)), padrao);
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
