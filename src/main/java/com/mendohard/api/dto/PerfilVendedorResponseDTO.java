package com.mendohard.api.dto;

/**
 * CU-04 — Respuesta GET /api/perfil/me para perfil Vendedor (UI 14).
 * Nunca expone hash de contraseña ni salt.
 */
public record PerfilVendedorResponseDTO(
        String vtelefono,
        String unombre,
        String uapellido,
        String uemail
) {}
