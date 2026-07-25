package com.mendohard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * CU-04 — Solicitud PUT /api/perfil/consumidor (UI 13).
 * {@code nuevaContrasenna} es opcional: si es null o blank, NO se modifica la contraseña.
 * Si falla cualquier @NotBlank/@Email, el GlobalExceptionHandler retorna DATA_INCONSISTENCY (UI 03).
 */
public record ModificarPerfilConsumidorRequestDTO(

        @NotBlank
        String capodo,

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
