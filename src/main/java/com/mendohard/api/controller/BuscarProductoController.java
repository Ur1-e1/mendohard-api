package com.mendohard.api.controller;

import com.mendohard.api.dto.CategoriaResponseDTO;
import com.mendohard.api.dto.ProductoResponseDTO;
import com.mendohard.api.service.BuscarProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos-busqueda")
@RequiredArgsConstructor
public class BuscarProductoController {

    private final BuscarProductoService buscarProductoService;

    /**
     * Paso 1: Al accionar "Buscar Componente de Hardware" en UI 02.
     * Devuelve la jerarquía de categorías activas para armar la UI 34.
     */
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResponseDTO>> obtenerCategorias() {
        List<CategoriaResponseDTO> categorias = buscarProductoService.obtenerCategoriasJerarquicas();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Paso 2: Al pulsar "Seleccionar" en UI 34.
     * Devuelve la lista de productos asociados a la categoría seleccionada para renderizar en UI 35.
     */
    @GetMapping("/categorias/{cCodigo}/productos")
    public ResponseEntity<List<ProductoResponseDTO>> obtenerProductosPorCategoria(
            @PathVariable("cCodigo") String cCodigo) {
        List<ProductoResponseDTO> productos = buscarProductoService.obtenerProductosPorCategoria(cCodigo);
        return ResponseEntity.ok(productos);
    }
}
