package com.mendohard.api.repository;


import com.mendohard.api.model.EstadoVendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoVendedorRepository extends JpaRepository<EstadoVendedor, Long> {

    @Query("SELECT ev FROM EstadoVendedor ev WHERE ev.EVNombre = :nombre AND ev.EVFechaBaja IS NULL")
    Optional<EstadoVendedor> findByNombreActivo(@Param("nombre") String nombre);
}
