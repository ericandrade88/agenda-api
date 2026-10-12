package com.agenciaviagem.agendaapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dados para o auto-cadastro de um novo usuario (sempre perfil CLIENTE). */
public record RegistroRequest(
        @NotBlank(message = "O nome e obrigatorio") String nome,
        @NotBlank(message = "O e-mail e obrigatorio") @Email(message = "E-mail invalido") String email,
        @NotBlank(message = "A senha e obrigatoria")
        @Size(min = 6, message = "A senha deve ter no minimo 6 caracteres") String senha) {
}
