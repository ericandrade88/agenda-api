package com.agenciaviagem.agendaapi.service;

import com.agenciaviagem.agendaapi.dto.ReservaRequest;
import com.agenciaviagem.agendaapi.model.Destino;
import com.agenciaviagem.agendaapi.model.Reserva;
import com.agenciaviagem.agendaapi.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Camada de servico responsavel pela reserva de pacotes de viagem
 * para um destino especifico.
 */
@Service
@Transactional
public class ReservaService {

    private final DestinoService destinoService;
    private final ReservaRepository reservaRepository;

    public ReservaService(DestinoService destinoService, ReservaRepository reservaRepository) {
        this.destinoService = destinoService;
        this.reservaRepository = reservaRepository;
    }

    /**
     * Reserva um pacote de viagem para o destino informado.
     * Lanca ResourceNotFoundException (via DestinoService) se o destino nao existir.
     */
    public Reserva reservar(Long destinoId, ReservaRequest request) {
        Destino destino = destinoService.buscarPorId(destinoId);

        Reserva reserva = new Reserva(
                destino,
                request.getNomeCliente(),
                request.getNumeroPessoas(),
                LocalDate.now()
        );
        return reservaRepository.save(reserva);
    }

    /**
     * Lista todas as reservas feitas para um determinado destino.
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarPorDestino(Long destinoId) {
        destinoService.buscarPorId(destinoId); // garante 404 se o destino nao existir
        return reservaRepository.findByDestino_Id(destinoId);
    }
}
