package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendedorDetalleResponseDTO {

    // Datos del Vendedor
    private String uCodigo;
    private String uNombre;
    private String uApellido;
    private String uEmail;
    private String vTelefono;
    private String vCuit;
    private String vRazonSocial;
    private String vCategoriaFiscal;

    // Datos del Comercio
    private String cNombreFantasia;
    private String cTelefono;
    private Float cLatitud;
    private Float cLongitud;
    private String cDireccionCalle;
    private String cNumeroEnCalle;
    private LocalDate cFechaSolicitud;
    private String cHorarioAtencion;

    // Datos Geográficos
    private String dNombre;
    private String proNombre;
    private String pNombre;
}
