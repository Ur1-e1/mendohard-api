package com.mendohard.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * CU-06: Cuerpo de la peticion de navegacion desde el menu principal (UI 19).
 */
public record OpcionNavegacionRequestDTO(

        @NotBlank(message = "La opcion seleccionada es obligatoria")
        String opcionSeleccionada
) {}