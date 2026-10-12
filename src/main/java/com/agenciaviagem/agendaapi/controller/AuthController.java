package com.agenciaviagem.agendaapi.controller;

import com.agenciaviagem.agendaapi.dto.RegistroRequest;
import com.agenciaviagem.agendaapi.model.Usuario;
import com.agenciaviagem.agendaapi.repository.PerfilRepository;
import com.agenciaviagem.agendaapi.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Cadastro publico de usuarios e consulta de "quem sou eu".
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository,
                          PerfilRepository perfilRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** POST /api/auth/registro - cria um usuario com perfil CLIENTE. */
    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("E-mail ja cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.getPerfis().add(perfilRepository.findByNome("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Perfil CLIENTE nao configurado")));

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRepository.save(usuario));
    }

    /** GET /api/auth/me - mostra o usuario autenticado e seus perfis. */
    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "email", authentication.getName(),
                "perfis", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());
    }
}
