package com.mendohard.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
public class ConsultaCincoCercanosRequestDTO {
    @NotBlank
    private String PCodigo;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Float CSLatitudDemanda;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Float CSLongitudDemanda;
}
