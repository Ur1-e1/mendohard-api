package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaStockPendienteResponseDTO {
    private Long csContador;
    private LocalDateTime csFechaHoraSolicitud;
    private LocalDateTime csFechaHoraExpiracion;
    private String pNombreTecnico;
    private Map<String, Object> pEspecificaciones;
}
