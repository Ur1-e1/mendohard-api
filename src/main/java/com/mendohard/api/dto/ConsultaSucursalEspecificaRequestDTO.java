package com.mendohard.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaSucursalEspecificaRequestDTO {
    @NotBlank
    private String PCodigo;

    @NotBlank
    private String CCodigo;
}
