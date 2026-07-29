package com.mendohard.api.repository;


import com.mendohard.api.model.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Long> {

    @Query("SELECT v FROM Vendedor v WHERE v.UEmail = :email AND v.UFechaBaja IS NULL")
    Optional<Vendedor> findByEmailActivo(@Param("email") String email);

    @Query("SELECT v FROM Vendedor v WHERE v.VCuit = :cuit AND v.UFechaBaja IS NULL")
    Optional<Vendedor> findByCuitActivo(@Param("cuit") String cuit);

    @Query("SELECT v FROM Vendedor v JOIN v.vendedorEstados ve JOIN ve.estadoVendedor ev " +
            "WHERE v.UFechaBaja IS NULL AND ve.VEFechaHasta IS NULL AND ev.EVNombre = 'VendedorAceptado' AND ev.EVFechaBaja IS NULL")
    List<Vendedor> findVendedoresAceptadosActivos();

    Optional<Vendedor> findByUCodigoAndUFechaBajaIsNull(String uCodigo);

    @Query("SELECT v FROM Vendedor v JOIN v.vendedorEstados ve JOIN ve.estadoVendedor ev " +
            "WHERE v.UFechaBaja IS NULL AND ve.VEFechaHasta IS NULL AND ev.EVNombre = 'VendedorPendiente' AND ev.EVFechaBaja IS NULL")
    List<Vendedor> findVendedoresPendientes();
}
