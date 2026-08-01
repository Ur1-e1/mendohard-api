package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComercioVendedorResponseDTO {
    private String cCodigo;
    private String cNombreFantasia;
    private String cTelefono;
    private String cDireccionCalle;
    private String cNumeroEnCalle;
    private String cHorarioAtencion;
    private String dNombre;
    private String proNombre;
    private String pNombre;
}
