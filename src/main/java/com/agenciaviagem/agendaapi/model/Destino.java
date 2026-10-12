package com.agenciaviagem.agendaapi.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um destino de viagem oferecido pela agencia.
 * Agora persistido em banco de dados (tabela "destinos").
 */
@Entity
@Table(name = "destinos")
public class Destino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String localizacao;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "preco_pacote", nullable = false)
    private Double precoPacote;

    // Controle da media de avaliacoes (nota de 1 a 10)
    @Column(name = "nota_media", nullable = false)
    private Double notaMedia = 0.0;

    @Column(name = "quantidade_avaliacoes", nullable = false)
    private Integer quantidadeAvaliacoes = 0;

    @OneToMany(mappedBy = "destino", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reserva> reservas = new ArrayList<>();

    public Destino() {
    }

    public Destino(String nome, String localizacao, String descricao, Double precoPacote) {
        this.nome = nome;
        this.localizacao = localizacao;
        this.descricao = descricao;
        this.precoPacote = precoPacote;
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
