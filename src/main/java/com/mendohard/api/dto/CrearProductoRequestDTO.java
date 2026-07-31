package com.mendohard.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearProductoRequestDTO {

    @NotBlank(message = "El nombre técnico no puede estar vacío")
    private String nombreTecnico;

    @NotNull(message = "Las especificaciones no pueden ser nulas")
    @NotEmpty(message = "Las especificaciones no pueden estar vacías")
    private Map<String, Object> especificaciones;

    @NotBlank(message = "La URL de la imagen no puede estar vacía")
    private String imagenUrl;

    @NotBlank(message = "El código de categoría no puede estar vacío")
    private String categoriaCodigo;
}
