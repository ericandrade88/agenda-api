package com.agenciaviagem.agendaapi.controller;

import com.agenciaviagem.agendaapi.dto.ReservaRequest;
import com.agenciaviagem.agendaapi.model.Reserva;
import com.agenciaviagem.agendaapi.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para reserva de pacotes de viagem vinculados a um destino.
 */
@RestController
@RequestMapping("/api/destinos/{destinoId}/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * Reserva um pacote de viagem para o destino informado.
     * POST /api/destinos/{destinoId}/reservas
     */
    @PostMapping
    public ResponseEntity<Reserva> reservar(@PathVariable Long destinoId, @Valid @RequestBody ReservaRequest request) {
        Reserva reserva = reservaService.reservar(destinoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    /**
     * Lista as reservas feitas para um determinado destino.
     * GET /api/destinos/{destinoId}/reservas
     */
    @GetMapping
    public ResponseEntity<List<Reserva>> listarPorDestino(@PathVariable Long destinoId) {
        return ResponseEntity.ok(reservaService.listarPorDestino(destinoId));
    }
}
