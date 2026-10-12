package com.agenciaviagem.agendaapi.config;

import com.agenciaviagem.agendaapi.model.Perfil;
import com.agenciaviagem.agendaapi.model.Usuario;
import com.agenciaviagem.agendaapi.repository.PerfilRepository;
import com.agenciaviagem.agendaapi.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Na primeira execucao cadastra no banco os perfis e um usuario de cada perfil,
 * para que seja possivel autenticar e testar a API.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final PerfilRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PerfilRepository perfilRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Perfil admin = garantirPerfil("ADMIN");
        Perfil cliente = garantirPerfil("CLIENTE");
        Perfil parceiro = garantirPerfil("PARCEIRO");

        garantirUsuario("Administrador", "admin@agencia.com", "admin123", admin);
        garantirUsuario("Cliente Exemplo", "cliente@agencia.com", "cliente123", cliente);
        garantirUsuario("Parceiro Exemplo", "parceiro@agencia.com", "parceiro123", parceiro);
    }

    private Perfil garantirPerfil(String nome) {
        return perfilRepository.findByNome(nome)
                .orElseGet(() -> perfilRepository.save(new Perfil(nome)));
    }

    private void garantirUsuario(String nome, String email, String senha, Perfil perfil) {
        if (!usuarioRepository.existsByEmail(email)) {
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(senha)); // nunca guardar senha em texto puro
            usuario.getPerfis().add(perfil);
            usuarioRepository.save(usuario);
        }
    }
}
