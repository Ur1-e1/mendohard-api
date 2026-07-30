package com.mendohard.api.service;

import com.mendohard.api.dto.CategoriaResponseDTO;
import com.mendohard.api.dto.ProductoResponseDTO;

import java.util.List;

public interface BuscarProductoService {
    
    /**
     * Obtiene el listado completo de categorías activas organizadas jerárquicamente.
     * (Corresponde a la entrada desde UI 02 hacia UI 34)
     */
    List<CategoriaResponseDTO> obtenerCategoriasJerarquicas();

    /**
     * Obtiene los productos activos pertenecientes a la categoría seleccionada por su CCodigo.
     * (Corresponde a la selección en UI 34 hacia UI 35)
     */
    List<ProductoResponseDTO> obtenerProductosPorCategoria(String cCodigo);
}
