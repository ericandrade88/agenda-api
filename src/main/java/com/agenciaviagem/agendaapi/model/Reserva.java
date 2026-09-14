package com.agenciaviagem.agendaapi.model;

import java.time.LocalDate;

/**
 * Representa a reserva de um pacote de viagem para um determinado destino.
 */
public class Reserva {

    private Long id;
    private Long destinoId;
    private String nomeCliente;
    private Integer numeroPessoas;
    private LocalDate dataReserva;

    public Reserva() {
    }

    public Reserva(Long id, Long destinoId, String nomeCliente, Integer numeroPessoas, LocalDate dataReserva) {
        this.id = id;
        this.destinoId = destinoId;
        this.nomeCliente = nomeCliente;
        this.numeroPessoas = numeroPessoas;
        this.dataReserva = dataReserva;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(Long destinoId) {
        this.destinoId = destinoId;
    }

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

    public LocalDate getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDate dataReserva) {
        this.dataReserva = dataReserva;
    }
}
