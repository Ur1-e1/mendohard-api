package com.mendohard.api.service.impl;

import com.mendohard.api.dto.*;
import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.Categoria;
import com.mendohard.api.model.Producto;
import com.mendohard.api.repository.CategoriaRepository;
import com.mendohard.api.repository.ProductoRepository;
import com.mendohard.api.service.ABMProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ABMProductoServiceImpl implements ABMProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaReducidaDTO> obtenerCategoriasActivas() {
        return categoriaRepository.findCategoriasHojasActivas().stream()
                .map(categoria -> CategoriaReducidaDTO.builder()
                        .codigo(categoria.getCCodigo())
                        .nombre(categoria.getCNombre())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDetalleDTO> listarProductosActivos() {
        return productoRepository.findByPFechaBajaIsNull().stream()
                .map(this::mapToProductoDetalleDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDetalleDTO obtenerProductoPorCodigo(String codigo) {
        Producto producto = productoRepository.findByPCodigoAndPFechaBajaIsNull(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con código " + codigo + " no existe o está dado de baja"));
        return mapToProductoDetalleDTO(producto);
    }

    @Override
    @Transactional
    public ProductoABMResponseDTO crearProducto(CrearProductoRequestDTO request) {
        String categoriaCodigo = request.getCategoriaCodigo().trim();
        Categoria categoria = categoriaRepository.findByCCodigoAndCFechaBajaIsNull(categoriaCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría con código " + categoriaCodigo + " no existe o está dada de baja"));

        String codigoGenerado = generarCodigoProductoUnico();

        Producto producto = Producto.builder()
                .PCodigo(codigoGenerado)
                .PNombreTecnico(request.getNombreTecnico().trim())
                .PEspecificaciones(request.getEspecificaciones())
                .PImagenUrl(request.getImagenUrl().trim())
                .PFechaAlta(LocalDate.now())
                .PFechaBaja(null)
                .categoria(categoria)
                .build();

        productoRepository.save(producto);
        log.info("Producto creado con éxito: {}", codigoGenerado);

        return ProductoABMResponseDTO.builder()
                .mensaje("El producto ha sido registrado y habilitado con éxito")
                .codigoProducto(codigoGenerado)
                .build();
    }

    @Override
    @Transactional
    public ProductoABMResponseDTO actualizarProducto(String codigo, ActualizarProductoRequestDTO request) {
        Producto producto = productoRepository.findByPCodigoAndPFechaBajaIsNull(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con código " + codigo + " no existe o está dado de baja"));

        String categoriaCodigo = request.getCategoriaCodigo().trim();
        Categoria categoria = categoriaRepository.findByCCodigoAndCFechaBajaIsNull(categoriaCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría con código " + categoriaCodigo + " no existe o está dada de baja"));

        producto.setPNombreTecnico(request.getNombreTecnico().trim());
        producto.setPEspecificaciones(request.getEspecificaciones());
        producto.setPImagenUrl(request.getImagenUrl().trim());
        producto.setCategoria(categoria);

        productoRepository.save(producto);
        log.info("Producto actualizado con éxito: {}", codigo);

        return ProductoABMResponseDTO.builder()
                .mensaje("El producto ha sido modificado correctamente")
                .codigoProducto(codigo)
                .build();
    }

    @Override
    @Transactional
    public ProductoABMResponseDTO deshabilitarProducto(String codigo) {
        Producto producto = productoRepository.findByPCodigoAndPFechaBajaIsNull(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("El producto con código " + codigo + " no existe o ya está dado de baja"));

        producto.setPFechaBaja(LocalDate.now());
        productoRepository.save(producto);
        log.info("Producto deshabilitado con éxito: {}", codigo);

        return ProductoABMResponseDTO.builder()
                .mensaje("El producto ha sido deshabilitado del catalogo con éxito")
                .codigoProducto(codigo)
                .build();
    }

    private String generarCodigoProductoUnico() {
        String codigo;
        do {
            codigo = "PRD-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        } while (productoRepository.existsByPCodigo(codigo));
        return codigo;
    }

    private ProductoDetalleDTO mapToProductoDetalleDTO(Producto producto) {
        return ProductoDetalleDTO.builder()
                .codigo(producto.getPCodigo())
                .nombreTecnico(producto.getPNombreTecnico())
                .especificaciones(producto.getPEspecificaciones())
                .imagenUrl(producto.getPImagenUrl())
                .categoriaCodigo(producto.getCategoria().getCCodigo())
                .categoriaNombre(producto.getCategoria().getCNombre())
                .build();
    }
}
