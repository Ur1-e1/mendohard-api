package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaStockRespuestaDTO {
    private Long csContador;
    private String estado;
    private LocalDateTime csFechaHoraRespuesta;
    private LocalDateTime csFechaHoraExpiracion;
    private String csMarcasRespuesta;
    private String csDescripcionRespuesta;
    private Float csPrecioRespuesta;
    private Integer csCantidadRespuesta;
}
