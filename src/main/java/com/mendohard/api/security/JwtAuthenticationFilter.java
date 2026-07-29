package com.mendohard.api.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String email;
        final String jwt;

        // Si la petición no trae la cabecera estándar de tokens 'Bearer ', se ignora el filtro y sigue su curso
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Cortamos la palabra "Bearer " para obtener solo el String del token
        email = jwtUtil.extraerEmail(jwt);

        // Si hay un email y el usuario no está ya autenticado en el hilo de ejecución actual
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (jwtUtil.esTokenValido(jwt, email)) {
                String rol = jwtUtil.extraerRol(jwt);
                List<String> permisos = jwtUtil.extraerPermisos(jwt);

                // Convertimos el rol en una autoridad entendible por Spring Security
                java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));

                // Agregamos también los permisos como autoridades
                if (permisos != null) {
                    permisos.forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, authorities
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Seteamos al usuario como AUTENTICADO. A partir de acá, pasa cualquier candado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
