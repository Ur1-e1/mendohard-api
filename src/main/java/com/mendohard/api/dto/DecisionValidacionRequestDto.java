package com.mendohard.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionValidacionRequestDto {
    @NotBlank(message = "La decisión de validación es requerida")
    private String decisionValidacion; // "Aceptar" o "Rechazar"
}
