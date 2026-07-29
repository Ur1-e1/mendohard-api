package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComercioPendienteResponseDto {
    // Datos Vendedor
    private String uCodigo;
    private String uNombre;
    private String uApellido;
    private String uEmail;
    private String vTelefono;
    private String vCuit;
    private String vRazonSocial;
    private String vCategoriaFiscal;

    // Datos Comercio
    private String cCodigo;
    private String cNombreFantasia;
    private String cTelefono;
    private Float cLatitud;
    private Float cLongitud;
    private String cDireccionCalle;
    private String cNumeroEnCalle;
    private LocalDate cFechaSolicitud;
    private String cHorarioAtencion;

    // Ubicación
    private String dNombre;
    private String proNombre;
    private String pNombre;
}
