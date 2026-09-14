package com.agenciaviagem.agendaapi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um destino de viagem oferecido pela agencia.
 * Nao ha persistencia em banco de dados: o repositorio em memoria
 * fica a cargo do DestinoService, conforme escopo do desafio.
 */
public class Destino {

    private Long id;
    private String nome;
    private String localizacao;
    private String descricao;
    private Double precoPacote;

    // Controle da media de avaliacoes (nota de 1 a 10)
    private Double notaMedia;
    private Integer quantidadeAvaliacoes;

    private List<Reserva> reservas = new ArrayList<>();

    public Destino() {
    }

    public Destino(Long id, String nome, String localizacao, String descricao, Double precoPacote) {
        this.id = id;
        this.nome = nome;
        this.localizacao = localizacao;
        this.descricao = descricao;
        this.precoPacote = precoPacote;
        this.notaMedia = 0.0;
        this.quantidadeAvaliacoes = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPrecoPacote() {
        return precoPacote;
    }

    public void setPrecoPacote(Double precoPacote) {
        this.precoPacote = precoPacote;
    }

    public Double getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(Double notaMedia) {
        this.notaMedia = notaMedia;
    }

    public Integer getQuantidadeAvaliacoes() {
        return quantidadeAvaliacoes;
    }

    public void setQuantidadeAvaliacoes(Integer quantidadeAvaliacoes) {
        this.quantidadeAvaliacoes = quantidadeAvaliacoes;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
