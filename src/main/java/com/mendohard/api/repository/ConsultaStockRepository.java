package com.mendohard.api.repository;

import com.mendohard.api.model.Comercio;
import com.mendohard.api.model.ConsultaStock;
import com.mendohard.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaStockRepository extends JpaRepository<ConsultaStock, Long> {
    List<ConsultaStock> findByComercioAndProducto(Comercio comercio, Producto producto);

    Optional<ConsultaStock> findFirstByComercioAndProductoOrderByCSFechaHoraSolicitudDesc(Comercio comercio, Producto producto);

    List<ConsultaStock> findByComercio_CCodigoAndEstadoConsultaStock_ECSNombreAndEstadoConsultaStock_ECSFechaBajaIsNull(String cCodigo, String ecsNombre);

    Optional<ConsultaStock> findByCSContador(Long csContador);

    @Modifying
    @Query("UPDATE ConsultaStock cs SET cs.estadoConsultaStock = :nuevoEstado " +
           "WHERE cs.estadoConsultaStock IN :estadosOrigen " +
           "AND cs.CSFechaHoraExpiracion <= :ahora")
    int extinguirConsultasVencidas(
        @org.springframework.data.repository.query.Param("nuevoEstado") com.mendohard.api.model.EstadoConsultaStock nuevoEstado,
        @org.springframework.data.repository.query.Param("estadosOrigen") List<com.mendohard.api.model.EstadoConsultaStock> estadosOrigen,
        @org.springframework.data.repository.query.Param("ahora") java.time.LocalDateTime ahora
    );
}
