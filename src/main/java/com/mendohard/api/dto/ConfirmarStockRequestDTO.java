package com.mendohard.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmarStockRequestDTO {
    
    private String csMarcasRespuesta;
    
    @NotBlank(message = "La descripción de la respuesta es obligatoria")
    private String csDescripcionRespuesta;
    
    private Float csPrecioRespuesta;
    
    @NotNull(message = "La cantidad de la respuesta es obligatoria")
    private Integer csCantidadRespuesta;
}
