package com.agenciaviagem.agendaapi.security;

import com.agenciaviagem.agendaapi.model.Usuario;
import com.agenciaviagem.agendaapi.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carrega o usuario do BANCO DE DADOS para a autenticacao.
 * Cada perfil (ADMIN, CLIENTE, PARCEIRO) vira a authority "ROLE_<perfil>".
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + email));

        String[] roles = usuario.getPerfis().stream()
                .map(perfil -> perfil.getNome())
                .toArray(String[]::new);

        return User.withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(roles)
                .disabled(!usuario.isAtivo())
                .build();
    }
}
