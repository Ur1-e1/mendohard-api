package com.mendohard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * CU-04 — Solicitud PUT /api/perfil/vendedor (UI 14).
 * {@code nuevaContrasenna} es opcional: si es null o blank, NO se modifica la contraseña.
 * Si falla cualquier @NotBlank/@Email, el GlobalExceptionHandler retorna DATA_INCONSISTENCY (UI 03).
 */
public record ModificarPerfilVendedorRequestDTO(

        @NotBlank
        String vtelefono,

        @NotBlank
        String unombre,

        @NotBlank
        String uapellido,

        @NotBlank
        @Email
        String uemail,

        // Opcional — null o blank significa "no cambiar contraseña"
        String nuevaContrasenna
) {}
