package com.mendohard.api.repository;

import com.mendohard.api.model.Categoria;
import com.mendohard.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos activos de una categoría dada
    List<Producto> findByCategoriaAndPFechaBajaIsNull(Categoria categoria);
}
