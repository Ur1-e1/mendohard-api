package com.mendohard.api.repository;


import com.mendohard.api.model.Clave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaveRepository extends JpaRepository<Clave, Long> {

    @Query("SELECT c FROM Clave c WHERE c.usuario.id = :usuarioId")
    Optional<Clave> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
