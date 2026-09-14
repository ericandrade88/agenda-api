package com.agenciaviagem.agendaapi.controller;

import com.agenciaviagem.agendaapi.dto.AvaliacaoRequest;
import com.agenciaviagem.agendaapi.dto.DestinoRequest;
import com.agenciaviagem.agendaapi.model.Destino;
import com.agenciaviagem.agendaapi.service.DestinoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST para gerenciamento de destinos de viagem.
 */
@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final DestinoService destinoService;

    public DestinoController(DestinoService destinoService) {
        this.destinoService = destinoService;
    }

    /**
     * 1. Cadastro de destino de viagem.
     * POST /api/destinos
     */
    @PostMapping
    public ResponseEntity<Destino> cadastrar(@Valid @RequestBody DestinoRequest request) {
        Destino novoDestino = destinoService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDestino);
    }

    /**
     * 2. Listagem de todos os destinos disponiveis.
     * GET /api/destinos
     */
    @GetMapping
    public ResponseEntity<List<Destino>> listarTodos() {
        return ResponseEntity.ok(destinoService.listarTodos());
    }

    /**
     * 3. Pesquisa de destinos por nome e/ou localizacao.
     * GET /api/destinos/pesquisa?nome=...&localizacao=...
     */
    @GetMapping("/pesquisa")
    public ResponseEntity<List<Destino>> pesquisar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String localizacao) {
        return ResponseEntity.ok(destinoService.pesquisar(nome, localizacao));
    }

    /**
     * 4. Visualizacao de informacoes detalhadas sobre um destino especifico.
     * GET /api/destinos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Destino> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(destinoService.buscarPorId(id));
    }

    /**
     * Atualizacao completa dos dados cadastrais de um destino.
     * PUT /api/destinos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Destino> atualizar(@PathVariable Long id, @Valid @RequestBody DestinoRequest request) {
        return ResponseEntity.ok(destinoService.atualizar(id, request));
    }

    /**
     * 5. Avaliacao de destino de viagem (nota de 1 a 10).
     * Recalcula a media do destino com base na nota ja existente.
     * PATCH /api/destinos/{id}/avaliacoes
     */
    @PatchMapping("/{id}/avaliacoes")
    public ResponseEntity<Destino> avaliar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest request) {
        return ResponseEntity.ok(destinoService.avaliar(id, request.getNota()));
    }

    /**
     * 6. Exclusao de um destino de viagem.
     * DELETE /api/destinos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        destinoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
