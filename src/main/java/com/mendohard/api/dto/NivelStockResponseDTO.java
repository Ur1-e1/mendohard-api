package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NivelStockResponseDTO {
    private String nsNombre;
    private Integer nsCantidadDesde;
    private Integer nsCantidadHasta;
}
