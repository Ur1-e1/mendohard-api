package com.mendohard.api.dto;

/**
 * CU-06: Respuesta con los datos publicos de un Rol activo.
 */
public record RolResponseDTO(
        String RCodigo,
        String RNombre,
        String RDescripcion
) {}