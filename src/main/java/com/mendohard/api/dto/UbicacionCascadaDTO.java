package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionCascadaDTO {

    private String departamentoCodigo;
    private String departamentoNombre;
    private String provinciaCodigo;
    private String provinciaNombre;
    private String paisCodigo;
    private String paisNombre;

}
