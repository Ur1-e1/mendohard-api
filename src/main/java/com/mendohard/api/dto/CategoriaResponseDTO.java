package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {
    private String CCodigo;
    private String CNombre;
    private String CCodigoPadre; // Código del padre (null si es categoría raíz)
    private boolean esHoja;       // true si no tiene subcategorías activas
    private List<CategoriaResponseDTO> subcategorias;
}
