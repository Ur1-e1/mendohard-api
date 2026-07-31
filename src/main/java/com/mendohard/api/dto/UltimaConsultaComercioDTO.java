package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UltimaConsultaComercioDTO {
    private LocalDateTime CSFechaHoraSolicitud;
    private LocalDateTime CSFechaHoraRespuesta;
    private String CSMarcasRespuesta;
    private String CSDescripcionRespuesta;
    private Float CSPrecioRespuesta;
    private String ECSNombre;
    private String NSCodigo;
    private String NSNombre;
}
