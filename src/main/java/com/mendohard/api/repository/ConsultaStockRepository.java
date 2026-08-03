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

    @Query("""
        SELECT new com.mendohard.api.dto.ComponenteDemandadoDTO(
            p.PNombreTecnico,
            COUNT(cs.id),
            p.PEspecificaciones
        )
        FROM Producto p
        LEFT JOIN ConsultaStock cs ON cs.producto = p 
            AND cs.CSFechaHoraRespuesta >= :fechaInicio 
            AND cs.CSFechaHoraRespuesta <= :fechaFin
        WHERE p.PFechaBaja IS NULL
        GROUP BY p.id, p.PNombreTecnico
        ORDER BY COUNT(cs.id) DESC
    """)
    List<com.mendohard.api.dto.ComponenteDemandadoDTO> findComponentesMasDemandados(
        @org.springframework.data.repository.query.Param("fechaInicio") java.time.LocalDateTime fechaInicio, 
        @org.springframework.data.repository.query.Param("fechaFin") java.time.LocalDateTime fechaFin
    );

    @Query("""
        SELECT new com.mendohard.api.dto.DemandaInsatisfechaDTO(
            p.PNombreTecnico,
            COUNT(cs.id),
            d.DNombre,
            p.PEspecificaciones
        )
        FROM ConsultaStock cs
        JOIN cs.producto p
        JOIN cs.comercio c
        JOIN c.departamento d
        JOIN cs.estadoConsultaStock ecs
        WHERE p.PFechaBaja IS NULL
          AND c.CFechaBaja IS NULL
          AND ecs.ECSFechaBaja IS NULL
          AND ecs.ECSNombre = 'SinStock'
          AND cs.CSFechaHoraRespuesta >= :fechaInicio 
          AND cs.CSFechaHoraRespuesta <= :fechaFin
        GROUP BY d.DNombre, p.id, p.PNombreTecnico
        ORDER BY d.DNombre ASC, COUNT(cs.id) DESC
    """)
    List<com.mendohard.api.dto.DemandaInsatisfechaDTO> findDemandaInsatisfechaPorZona(
        @org.springframework.data.repository.query.Param("fechaInicio") java.time.LocalDateTime fechaInicio, 
        @org.springframework.data.repository.query.Param("fechaFin") java.time.LocalDateTime fechaFin
    );
}
