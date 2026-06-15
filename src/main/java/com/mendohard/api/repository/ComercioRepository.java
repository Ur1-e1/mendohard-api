package com.mendohard.api.repository;


import com.mendohard.api.model.Comercio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComercioRepository extends JpaRepository<Comercio, Long> {

    @Query("SELECT c FROM Comercio c WHERE c.CCodigo = :codigo")
    Optional<Comercio> findByCodigoComercio(@Param("codigo") String codigo);
}
