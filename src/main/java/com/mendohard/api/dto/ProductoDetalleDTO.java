package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDetalleDTO {
    private String codigo;
    private String nombreTecnico;
    private Map<String, Object> especificaciones;
    private String imagenUrl;
    private String categoriaCodigo;
    private String categoriaNombre;
}
