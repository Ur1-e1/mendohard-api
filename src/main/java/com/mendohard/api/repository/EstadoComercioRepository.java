package com.mendohard.api.repository;


import com.mendohard.api.model.EstadoComercio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoComercioRepository extends JpaRepository<EstadoComercio, Long> {

    @Query("SELECT ec FROM EstadoComercio ec WHERE ec.ECNombre = :nombre AND ec.ECFechaBaja IS NULL")
    Optional<EstadoComercio> findByNombreActivo(@Param("nombre") String nombre);
}
