package com.mendohard.api.repository;

import com.mendohard.api.model.Comercio;
import com.mendohard.api.model.ConsultaStock;
import com.mendohard.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaStockRepository extends JpaRepository<ConsultaStock, Long> {
    List<ConsultaStock> findByComercioAndProducto(Comercio comercio, Producto producto);

    Optional<ConsultaStock> findFirstByComercioAndProductoOrderByCSFechaHoraSolicitudDesc(Comercio comercio, Producto producto);
}
