package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedorInhabilitarResponseDto {
    private String uCodigo;
    private String uNombre;
    private String uApellido;
    private String uEmail;
    private String vTelefono;
    private String vCuit;
    private String vRazonSocial;
    private String vCategoriaFiscal;
}
