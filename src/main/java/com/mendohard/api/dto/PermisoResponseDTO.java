package com.mendohard.api.dto;

/**
 * CU-06: Respuesta con los datos publicos de un Permiso activo.
 */
public record PermisoResponseDTO(
        String PCodigo,
        String PNombre,
        String PDescripcion
) {}