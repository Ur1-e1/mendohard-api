package com.mendohard.api.repository;

import com.mendohard.api.model.EstadoConsultaStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoConsultaStockRepository extends JpaRepository<EstadoConsultaStock, Long> {
    Optional<EstadoConsultaStock> findByECSNombreAndECSFechaBajaIsNull(String ECSNombre);
}
