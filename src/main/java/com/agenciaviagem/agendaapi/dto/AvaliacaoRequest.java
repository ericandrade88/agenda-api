package com.agenciaviagem.agendaapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Dados recebidos ao avaliar um destino (nota de 1 a 10).
 */
public class AvaliacaoRequest {

    @NotNull(message = "A nota e obrigatoria")
    @Min(value = 1, message = "A nota minima e 1")
    @Max(value = 10, message = "A nota maxima e 10")
    private Integer nota;

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }
}
