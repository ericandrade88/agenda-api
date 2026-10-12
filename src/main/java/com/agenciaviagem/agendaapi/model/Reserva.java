package com.agenciaviagem.agendaapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Representa a reserva de um pacote de viagem para um determinado destino.
 * Persistida na tabela "reservas", ligada a "destinos" por chave estrangeira.
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destino_id")
    private Destino destino;

    @Column(name = "nome_cliente", nullable = false)
    private String nomeCliente;

    @Column(name = "numero_pessoas", nullable = false)
    private Integer numeroPessoas;

    @Column(name = "data_reserva", nullable = false)
    private LocalDate dataReserva;

    public Reserva() {
    }

    public Reserva(Destino destino, String nomeCliente, Integer numeroPessoas, LocalDate dataReserva) {
        this.destino = destino;
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

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }

    /** Mantem o JSON da API igual ao do desafio 1: expoe apenas o id do destino. */
    @JsonProperty("destinoId")
    public Long getDestinoId() {
        return destino != null ? destino.getId() : null;
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
