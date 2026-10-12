package com.agenciaviagem.agendaapi.repository;

import com.agenciaviagem.agendaapi.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByDestino_Id(Long destinoId);
}
