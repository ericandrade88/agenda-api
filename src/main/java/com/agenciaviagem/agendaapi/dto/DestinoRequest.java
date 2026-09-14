package com.agenciaviagem.agendaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Dados recebidos ao cadastrar (ou atualizar) um destino de viagem.
 */
public class DestinoRequest {

    @NotBlank(message = "O nome do destino e obrigatorio")
    private String nome;

    @NotBlank(message = "A localizacao do destino e obrigatoria")
    private String localizacao;

    private String descricao;

    @NotNull(message = "O preco do pacote e obrigatorio")
    @Positive(message = "O preco do pacote deve ser maior que zero")
    private Double precoPacote;

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
}
