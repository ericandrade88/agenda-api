package com.agenciaviagem.agendaapi.repository;

import com.agenciaviagem.agendaapi.model.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByNome(String nome);
}
