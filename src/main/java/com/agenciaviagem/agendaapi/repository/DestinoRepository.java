package com.agenciaviagem.agendaapi.repository;

import com.agenciaviagem.agendaapi.model.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** JpaSpecificationExecutor permite montar a pesquisa por nome e/ou localizacao dinamicamente. */
public interface DestinoRepository extends JpaRepository<Destino, Long>, JpaSpecificationExecutor<Destino> {
}
