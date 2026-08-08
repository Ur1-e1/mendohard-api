package com.mendohard.api.repository;

import com.mendohard.api.model.Categoria;
import com.mendohard.api.model.NivelStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NivelStockRepository extends JpaRepository<NivelStock, Long> {
    Optional<NivelStock> findByCategoriaAndNSFechaBajaIsNull(Categoria categoria);

    List<NivelStock> findByCategoria_IdAndNSFechaBajaIsNull(Long categoriaId);

    @Query("""
        SELECT ns FROM NivelStock ns
        WHERE ns.categoria = :categoria
          AND ns.NSFechaBaja IS NULL
          AND ns.NSCantidadDesde <= :cantidad
          AND (ns.NSCantidadHasta IS NULL OR ns.NSCantidadHasta >= :cantidad)
        """)
    Optional<NivelStock> findNivelStockActivoPorCategoriaYCantidad(
        @Param("categoria") Categoria categoria,
        @Param("cantidad") Integer cantidad
    );
}
