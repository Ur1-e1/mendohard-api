package com.mendohard.api.repository;


import com.mendohard.api.model.Comercio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComercioRepository extends JpaRepository<Comercio, Long> {

    @Query("SELECT c FROM Comercio c WHERE c.CCodigo = :codigo")
    Optional<Comercio> findByCodigoComercio(@Param("codigo") String codigo);

    @Query("SELECT c FROM Comercio c " +
           "JOIN c.comercioEstados ce " +
           "JOIN ce.estadoComercio ec " +
           "JOIN Vendedor v ON c MEMBER OF v.comercios " +
           "JOIN v.vendedorEstados ve " +
           "JOIN ve.estadoVendedor ev " +
           "WHERE c.CFechaAlta IS NULL AND c.CFechaBaja IS NULL " +
           "AND ce.CEFechaHasta IS NULL AND ec.ECNombre = 'ComercioPendiente' AND ec.ECFechaBaja IS NULL " +
           "AND v.UFechaBaja IS NULL " +
           "AND ve.VEFechaHasta IS NULL AND ev.EVNombre = 'VendedorAceptado' AND ev.EVFechaBaja IS NULL")
    List<Comercio> findComerciosPendientesDeVendedoresAceptados();

    List<Comercio> findByCFechaBajaIsNullAndCFechaAltaIsNotNull();
}
