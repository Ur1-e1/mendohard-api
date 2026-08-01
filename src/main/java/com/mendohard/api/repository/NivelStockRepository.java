package com.mendohard.api.repository;

import com.mendohard.api.model.Categoria;
import com.mendohard.api.model.NivelStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface NivelStockRepository extends JpaRepository<NivelStock, Long> {
    Optional<NivelStock> findByCategoriaAndNSFechaBajaIsNull(Categoria categoria);

    List<NivelStock> findByCategoria_IdAndNSFechaBajaIsNull(Long categoriaId);
}
