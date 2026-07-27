package com.mendohard.api.repository;

import com.mendohard.api.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

    @Query("SELECT r FROM Rol r WHERE r.RNombre = :nombre AND r.RFechaBaja IS NULL")
    Optional<Rol> findByRNombreActivo(@Param("nombre") String nombre);

    // CU-06: Devuelve todos los roles con fecha de baja nula (activos)
    List<Rol> findAllByRFechaBajaIsNull();

    // CU-06: Busca un rol activo por su código único
    Optional<Rol> findByRCodigoAndRFechaBajaIsNull(String rCodigo);
}
