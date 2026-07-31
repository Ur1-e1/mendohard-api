package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComercioResumenDTO {
    private String CCodigo;
    private String CNombreFantasia;
    private String CTelefono;
    private String CDireccionCalle;
    private String CNumeroEnCalle;
    private String CHorarioAtencion;
    private Float CLatitud;
    private Float CLongitud;
}
