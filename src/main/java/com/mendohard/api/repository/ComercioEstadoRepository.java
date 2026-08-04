package com.mendohard.api.repository;


import com.mendohard.api.model.ComercioEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ComercioEstadoRepository extends JpaRepository<ComercioEstado, Long> {

    // UI_06: Contar comercios activos (Comercio no dado de baja y estado actual Aceptado)
    @Query("SELECT COUNT(ce) FROM Comercio c JOIN c.comercioEstados ce WHERE c.CFechaBaja IS NULL AND ce.CEFechaHasta IS NULL AND ce.estadoComercio.ECNombre = 'ComercioAceptado'")
    Long countComerciosActivos();
}
