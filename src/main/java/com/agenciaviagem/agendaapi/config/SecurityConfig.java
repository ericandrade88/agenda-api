package com.agenciaviagem.agendaapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Autorizacao por perfil de acesso:
 *
 *  ADMIN    -> tudo: cadastrar/atualizar/excluir destinos, ver reservas, gerir usuarios
 *  CLIENTE  -> consulta destinos, avalia destinos e reserva pacotes
 *  PARCEIRO -> somente consulta de destinos (integracao de terceiros)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // publicos
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/registro").permitAll()

                // reservas e avaliacoes (regras especificas vem ANTES das genericas)
                .requestMatchers(HttpMethod.GET, "/api/destinos/*/reservas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/destinos/*/reservas").hasAnyRole("CLIENTE", "ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/destinos/*/avaliacoes").hasAnyRole("CLIENTE", "ADMIN")

                // destinos: leitura para qualquer perfil, escrita so ADMIN
                .requestMatchers(HttpMethod.GET, "/api/destinos/**").hasAnyRole("ADMIN", "CLIENTE", "PARCEIRO")
                .requestMatchers(HttpMethod.POST, "/api/destinos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/destinos/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/destinos/*").hasRole("ADMIN")

                // gestao de usuarios
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // qualquer outra rota exige autenticacao
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
