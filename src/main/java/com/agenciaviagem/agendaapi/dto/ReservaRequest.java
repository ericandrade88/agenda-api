package com.agenciaviagem.agendaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Dados recebidos ao reservar um pacote de viagem para um destino.
 */
public class ReservaRequest {

    @NotBlank(message = "O nome do cliente e obrigatorio")
    private String nomeCliente;

    @NotNull(message = "O numero de pessoas e obrigatorio")
    @Positive(message = "O numero de pessoas deve ser maior que zero")
    private Integer numeroPessoas;

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public Integer getNumeroPessoas() {
        return numeroPessoas;
    }

    public void setNumeroPessoas(Integer numeroPessoas) {
        this.numeroPessoas = numeroPessoas;
    }
}
