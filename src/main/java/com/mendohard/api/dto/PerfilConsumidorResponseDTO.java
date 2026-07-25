package com.mendohard.api.dto;

/**
 * CU-04 — Respuesta GET /api/perfil/me para perfil Consumidor (UI 13).
 * Nunca expone hash de contraseña ni salt.
 */
public record PerfilConsumidorResponseDTO(
        String capodo,
        String unombre,
        String uapellido,
        String uemail
) {}
