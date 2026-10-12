package com.agenciaviagem.agendaapi.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/** Nomes dos perfis desejados para um usuario: ADMIN, CLIENTE, PARCEIRO. */
public record PerfisRequest(@NotEmpty(message = "Informe ao menos um perfil") Set<String> perfis) {
}
