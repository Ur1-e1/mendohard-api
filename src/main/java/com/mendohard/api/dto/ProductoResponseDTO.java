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
public class ProductoResponseDTO {
    private String PCodigo;
    private String PNombreTecnico;
    private Map<String, Object> PEspecificaciones;
    private String PImagenUrl;
}
