package com.mendohard.api.service;

import com.mendohard.api.dto.*;
import java.util.List;

public interface ABMProductoService {
    List<CategoriaReducidaDTO> obtenerCategoriasActivas();
    List<ProductoDetalleDTO> listarProductosActivos();
    ProductoDetalleDTO obtenerProductoPorCodigo(String codigo);
    ProductoABMResponseDTO crearProducto(CrearProductoRequestDTO request);
    ProductoABMResponseDTO actualizarProducto(String codigo, ActualizarProductoRequestDTO request);
    ProductoABMResponseDTO deshabilitarProducto(String codigo);
}
