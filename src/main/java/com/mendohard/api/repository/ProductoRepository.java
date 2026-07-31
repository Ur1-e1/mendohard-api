package com.mendohard.api.repository;

import com.mendohard.api.model.Categoria;
import com.mendohard.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos activos de una categoría dada
    List<Producto> findByCategoriaAndPFechaBajaIsNull(Categoria categoria);

    // Buscar producto activo por su código
    Optional<Producto> findByPCodigoAndPFechaBajaIsNull(String PCodigo);

    // Listar todos los productos activos
    List<Producto> findByPFechaBajaIsNull();

    // Verificar si un código de producto existe
    boolean existsByPCodigo(String PCodigo);
}
