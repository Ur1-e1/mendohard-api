package com.mendohard.api.repository;


import com.mendohard.api.model.VendedorEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorEstadoRepository extends JpaRepository<VendedorEstado, Long> {

    // UI_06: Contar vendedores con estado actual Pendiente
    @Query("SELECT COUNT(ve) FROM VendedorEstado ve WHERE ve.VEFechaHasta IS NULL AND ve.estadoVendedor.EVNombre = 'VendedorPendiente'")
    Long countVendedoresPendientes();
}
