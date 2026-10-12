package com.agenciaviagem.agendaapi.model;

import jakarta.persistence.*;

/** Perfil de acesso armazenado no banco: ADMIN, CLIENTE ou PARCEIRO. */
@Entity
@Table(name = "perfis")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nome;

    public Perfil() {
    }

    public Perfil(String nome) {
        this.nome = nome;
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
}
