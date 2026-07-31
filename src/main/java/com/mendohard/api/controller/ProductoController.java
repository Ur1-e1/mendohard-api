package com.mendohard.api.controller;

import com.mendohard.api.dto.*;
import com.mendohard.api.service.ABMProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ABMProductoService abmProductoService;

    @GetMapping("/categorias-activas")
    public ResponseEntity<List<CategoriaReducidaDTO>> obtenerCategoriasActivas() {
        return ResponseEntity.ok(abmProductoService.obtenerCategoriasActivas());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProductoDetalleDTO>> listarProductosActivos() {
        return ResponseEntity.ok(abmProductoService.listarProductosActivos());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<ProductoDetalleDTO> obtenerProductoPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(abmProductoService.obtenerProductoPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<ProductoABMResponseDTO> crearProducto(@Valid @RequestBody CrearProductoRequestDTO request) {
        ProductoABMResponseDTO response = abmProductoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<ProductoABMResponseDTO> actualizarProducto(
            @PathVariable String codigo,
            @Valid @RequestBody ActualizarProductoRequestDTO request) {
        return ResponseEntity.ok(abmProductoService.actualizarProducto(codigo, request));
    }

    @PatchMapping("/{codigo}/deshabilitar")
    public ResponseEntity<ProductoABMResponseDTO> deshabilitarProducto(@PathVariable String codigo) {
        return ResponseEntity.ok(abmProductoService.deshabilitarProducto(codigo));
    }
}
