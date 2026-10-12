package com.agenciaviagem.agendaapi.controller;

import com.agenciaviagem.agendaapi.dto.PerfisRequest;
import com.agenciaviagem.agendaapi.exception.ResourceNotFoundException;
import com.agenciaviagem.agendaapi.model.Perfil;
import com.agenciaviagem.agendaapi.model.Usuario;
import com.agenciaviagem.agendaapi.repository.PerfilRepository;
import com.agenciaviagem.agendaapi.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Gestao de usuarios - acesso restrito ao perfil ADMIN (ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, PerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado com id: " + id));
    }

    /** PUT /api/usuarios/{id}/perfis - troca os perfis do usuario. */
    @PutMapping("/{id}/perfis")
    public Usuario alterarPerfis(@PathVariable Long id, @Valid @RequestBody PerfisRequest request) {
        Usuario usuario = buscar(id);
        Set<Perfil> novosPerfis = new HashSet<>();
        for (String nome : request.perfis()) {
            novosPerfis.add(perfilRepository.findByNome(nome.toUpperCase())
                    .orElseThrow(() -> new IllegalArgumentException("Perfil inexistente: " + nome)));
        }
        usuario.setPerfis(novosPerfis);
        return usuarioRepository.save(usuario);
    }

    /** DELETE /api/usuarios/{id} - desativa o usuario (ele deixa de conseguir autenticar). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        Usuario usuario = buscar(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        return ResponseEntity.noContent().build();
    }
}
