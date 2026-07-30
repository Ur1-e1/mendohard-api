package com.mendohard.api.service;

import com.mendohard.api.dto.CategoriaResponseDTO;
import com.mendohard.api.dto.ProductoResponseDTO;
import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.Categoria;
import com.mendohard.api.model.Producto;
import com.mendohard.api.repository.CategoriaRepository;
import com.mendohard.api.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuscarProductoServiceImpl implements BuscarProductoService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public List<CategoriaResponseDTO> obtenerCategoriasJerarquicas() {
        List<Categoria> categoriasActivas = categoriaRepository.findByCFechaBajaIsNull();

        return categoriasActivas.stream()
                // Solo procesamos las categorías raíz (sin padre) para construir el árbol desde arriba
                .filter(c -> c.getCategoriaPadre() == null)
                .map(this::mapearACategoriaResponseDTO)
                .collect(Collectors.toList());
    }

    private CategoriaResponseDTO mapearACategoriaResponseDTO(Categoria categoria) {
        // Filtrar subcategorías que estén activas
        List<Categoria> subcategoriasActivas = categoria.getSubcategorias().stream()
                .filter(sub -> sub.getCFechaBaja() == null)
                .collect(Collectors.toList());

        List<CategoriaResponseDTO> subcategoriasDTO = subcategoriasActivas.stream()
                .map(this::mapearACategoriaResponseDTO)
                .collect(Collectors.toList());

        boolean esHoja = subcategoriasActivas.isEmpty();

        String codigoPadre = categoria.getCategoriaPadre() != null ? categoria.getCategoriaPadre().getCCodigo() : null;

        return CategoriaResponseDTO.builder()
                .CCodigo(categoria.getCCodigo())
                .CNombre(categoria.getCNombre())
                .CCodigoPadre(codigoPadre)
                .esHoja(esHoja)
                .subcategorias(subcategoriasDTO)
                .build();
    }

    @Override
    public List<ProductoResponseDTO> obtenerProductosPorCategoria(String cCodigo) {
        String codigoSanitizado = cCodigo != null ? cCodigo.trim() : "";

        Categoria categoria = categoriaRepository.findByCCodigoAndCFechaBajaIsNull(codigoSanitizado)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada o inactiva: " + codigoSanitizado));

        List<Producto> productosActivos = productoRepository.findByCategoriaAndPFechaBajaIsNull(categoria);

        return productosActivos.stream()
                .map(p -> ProductoResponseDTO.builder()
                        .PCodigo(p.getPCodigo())
                        .PNombreTecnico(p.getPNombreTecnico())
                        .PEspecificaciones(p.getPEspecificaciones())
                        .PImagenUrl(p.getPImagenUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
