package com.mendohard.api.security;



import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Cambiamos la política a STATELESS. La API ya no guarda sesiones en memoria server-side, usa tokens.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/iniciar-sesion").permitAll() // Público
                        .requestMatchers("/h2-console/**").permitAll() // Desarrollo
                        .requestMatchers("/swagger-ui.html", "/v3/api-docs", "/swagger-ui/**").permitAll() // Docs
                        .anyRequest().authenticated() // Candado para TODO lo demás que crees a partir de ahora
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .formLogin(AbstractHttpConfigurer::disable);

        // 💡 CLAVE: Le decimos a Spring que ejecute nuestro filtro JWT antes del filtro de login por defecto
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}