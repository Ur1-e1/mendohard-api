package com.mendohard.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarComercioRequestDTO {

    @NotBlank(message = "El nombre de fantasía es obligatorio")
    private String nombreFantasia;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotNull(message = "La latitud es obligatoria")
    private Float latitud;

    @NotNull(message = "La longitud es obligatoria")
    private Float longitud;

    @NotBlank(message = "La dirección de calle es obligatoria")
    private String direccionCalle;

    @NotBlank(message = "El número en calle es obligatorio")
    private String numeroEnCalle;

    @NotBlank(message = "El horario de atención es obligatorio")
    private String horarioAtencion;

    @NotBlank(message = "El código del departamento es obligatorio")
    private String departamentoCodigo;
}
