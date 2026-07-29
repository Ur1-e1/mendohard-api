package com.mendohard.api.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/iniciar-sesion").permitAll()
                        .requestMatchers("/api/v1/auth/registro/consumidor").permitAll()
                        .requestMatchers("/api/v1/auth/registro/vendedor").permitAll()
                        .requestMatchers("/api/v1/auth/registro/vendedor/ubicaciones").permitAll()
                        // CU-05: Recuperar Credencial — endpoints públicos (usuario sin JWT activo)
                        .requestMatchers("/api/v1/auth/recuperar-credencial/solicitar").permitAll()
                        .requestMatchers("/api/v1/auth/recuperar-credencial/restablecer").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/v3/api-docs", "/swagger-ui/**").permitAll()
                        // CU-04: GET /api/perfil/me → Consumidor o Vendedor pueden consultar su perfil
                        .requestMatchers(HttpMethod.GET, "/api/perfil/me").hasAnyRole("Consumidor", "Vendedor")
                        // CU-04: PUT /api/perfil/consumidor → exclusivo del rol Consumidor
                        .requestMatchers(HttpMethod.PUT, "/api/perfil/consumidor").hasRole("Consumidor")
                        // CU-04: PUT /api/perfil/vendedor → exclusivo del rol Vendedor
                        .requestMatchers(HttpMethod.PUT, "/api/perfil/vendedor").hasRole("Vendedor")
                        // CU-06: Gestionar Roles → exclusivo del Responsable MendoHard
                        .requestMatchers("/api/v1/gestionar-roles/**").hasRole("Responsable MendoHard")
                        // CU-07: Inhabilitar Usuario → exclusivo del Responsable MendoHard
                        .requestMatchers("/api/v1/usuarios/inhabilitar/**").hasRole("Responsable MendoHard")
                        // CU-08: Validar Vendedor → exclusivo del Responsable MendoHard
                        .requestMatchers("/api/v1/vendedores/**").hasRole("Responsable MendoHard")
                        // CU-09: Validar Comercio → exclusivo del Responsable MendoHard
                        .requestMatchers(HttpMethod.GET, "/api/v1/comercios/validar/**").hasRole("Responsable MendoHard")
                        .requestMatchers(HttpMethod.POST, "/api/v1/comercios/validar/**").hasRole("Responsable MendoHard")
                        // CU-10: Registrar Comercio → exclusivo de quien posea el permiso
                        .requestMatchers("/api/v1/comercios/**").hasAuthority("registrar_comercio")
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .formLogin(AbstractHttpConfigurer::disable)
                // Registrar el handler custom para que los 403 del filtro usen el DTO estandarizado
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(customAccessDeniedHandler)
                );

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}