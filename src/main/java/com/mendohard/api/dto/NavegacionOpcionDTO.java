package com.mendohard.api.dto;

/**
 * CU-06: Respuesta de navegacion hacia una UI destino desde el menu principal (UI 19).
 */
public record NavegacionOpcionDTO(
        String opcionSeleccionada,
        String mensaje
) {}