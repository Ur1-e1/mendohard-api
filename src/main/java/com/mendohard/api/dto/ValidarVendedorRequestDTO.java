package com.mendohard.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidarVendedorRequestDTO {

    @NotBlank(message = "El código de usuario es obligatorio.")
    private String uCodigo;

    @NotBlank(message = "La decisión de validación es obligatoria.")
    private String decisionValidacion;
}
