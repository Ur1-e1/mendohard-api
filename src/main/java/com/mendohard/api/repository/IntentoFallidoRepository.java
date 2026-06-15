package com.mendohard.api.repository;


import com.mendohard.api.model.IntentoFallido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface IntentoFallidoRepository extends JpaRepository<IntentoFallido, Long> {

    @Query("SELECT i FROM IntentoFallido i WHERE i.usuario.id = :usuarioId AND i.IFFecha = :fecha")
    Optional<IntentoFallido> findByUsuarioIdAndFecha(@Param("usuarioId") Long usuarioId, @Param("fecha") LocalDate fecha);
}