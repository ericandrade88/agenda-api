package com.agenciaviagem.agendaapi.service;

import com.agenciaviagem.agendaapi.dto.ReservaRequest;
import com.agenciaviagem.agendaapi.model.Destino;
import com.agenciaviagem.agendaapi.model.Reserva;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Camada de servico responsavel pela reserva de pacotes de viagem
 * para um destino especifico.
 */
@Service
public class ReservaService {

    private final DestinoService destinoService;
    private final AtomicLong sequenciaId = new AtomicLong(1);

    public ReservaService(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    /**
     * Reserva um pacote de viagem para o destino informado.
     * Lanca ResourceNotFoundException (via DestinoService) se o destino nao existir.
     */
    public Reserva reservar(Long destinoId, ReservaRequest request) {
        Destino destino = destinoService.buscarPorId(destinoId);

        Reserva reserva = new Reserva(
                sequenciaId.getAndIncrement(),
                destino.getId(),
                request.getNomeCliente(),
                request.getNumeroPessoas(),
                LocalDate.now()
        );

        destino.getReservas().add(reserva);
        return reserva;
    }

    /**
     * Lista todas as reservas feitas para um determinado destino.
     */
    public List<Reserva> listarPorDestino(Long destinoId) {
        Destino destino = destinoService.buscarPorId(destinoId);
        return destino.getReservas();
    }
}
