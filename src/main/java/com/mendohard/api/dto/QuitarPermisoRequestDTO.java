package com.mendohard.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * CU-06: Cuerpo de la peticion para quitar un permiso de un rol (baja logica).
 */
public record QuitarPermisoRequestDTO(

        @NotBlank(message = "El codigo de rol es obligatorio")
        String RCodigo,

        @NotBlank(message = "El codigo de permiso es obligatorio")
        String PCodigo
) {}