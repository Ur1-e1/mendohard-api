package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComercioResponseDTO {

    private String codigo;
    private String nombreFantasia;
    private String telefono;
    private String direccionCalle;
    private String numeroEnCalle;
    private String horarioAtencion;
    private LocalDate fechaSolicitud;
    private String estadoActual;
    private String departamentoNombre;
    private String provinciaNombre;
    private String paisNombre;

}
