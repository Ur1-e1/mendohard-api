package com.mendohard.api.controller;

import com.mendohard.api.dto.ModificarPerfilConsumidorRequestDTO;
import com.mendohard.api.dto.ModificarPerfilVendedorRequestDTO;
import com.mendohard.api.service.ModificarPerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * CU-04 Modificar Perfil — Controller REST.
 * <p>
 * Precondición de autenticación: delegada a Spring Security
 * (JwtAuthenticationFilter + SecurityConfig).
 * Solo usuarios con rol 'Consumidor' o 'Vendedor' acceden a estos endpoints.
 */
@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
@Slf4j
public class PerfilController {

    private final ModificarPerfilService modificarPerfilService;

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/perfil/me — Pre-carga datos para UI 13 (Consumidor) y UI 14
    // (Vendedor)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Retorna el perfil actual del usuario autenticado.
     * La respuesta varía según el tipo: {@code PerfilConsumidorResponseDTO} o
     * {@code PerfilVendedorResponseDTO}.
     *
     * @return 200 OK con el DTO de perfil correspondiente.
     */
    @GetMapping("/me")
    public ResponseEntity<Object> obtenerPerfilActual() {
        String email = obtenerEmailDelContexto();
        log.info("GET /api/perfil/me — usuario: {}", email);

        Object perfil = modificarPerfilService.obtenerPerfilActual(email);
        return ResponseEntity.ok(perfil);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/perfil/consumidor — Modificación de perfil UI 13
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Actualiza los datos del perfil del Consumidor autenticado (UI 13).
     * Incluye tratamiento condicional de nueva contraseña (CA N°2).
     *
     * @param request DTO con los nuevos datos validados
     * @return 204 No Content si la operación fue exitosa
     */
    @PutMapping("/consumidor")
    public ResponseEntity<Void> modificarPerfilConsumidor(
            @Valid @RequestBody ModificarPerfilConsumidorRequestDTO request) {
        String email = obtenerEmailDelContexto();
        log.info("PUT /api/perfil/consumidor — usuario: {}", email);

        modificarPerfilService.modificarPerfilConsumidor(email, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/perfil/vendedor — Modificación de perfil UI 14
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Actualiza los datos del perfil del Vendedor autenticado (UI 14).
     * Incluye tratamiento condicional de nueva contraseña (CA N°6).
     *
     * @param request DTO con los nuevos datos validados
     * @return 204 No Content si la operación fue exitosa
     */
    @PutMapping("/vendedor")
    public ResponseEntity<Void> modificarPerfilVendedor(
            @Valid @RequestBody ModificarPerfilVendedorRequestDTO request) {
        String email = obtenerEmailDelContexto();
        log.info("PUT /api/perfil/vendedor — usuario: {}", email);

        modificarPerfilService.modificarPerfilVendedor(email, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper — Extrae el email (principal) del SecurityContext poblado por
    // JwtAuthenticationFilter
    // ─────────────────────────────────────────────────────────────────────────

    private String obtenerEmailDelContexto() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
