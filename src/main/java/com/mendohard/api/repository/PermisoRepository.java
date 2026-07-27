package com.mendohard.api.repository;

import com.mendohard.api.model.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    // CU-06: Devuelve todos los permisos con fecha de baja nula (activos)
    List<Permiso> findAllByPFechaBajaIsNull();

    // CU-06: Busca un permiso activo por su código único
    Optional<Permiso> findByPCodigoAndPFechaBajaIsNull(String pCodigo);
}
