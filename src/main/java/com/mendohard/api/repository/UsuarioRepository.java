package com.mendohard.api.repository;


import com.mendohard.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u " +
            "JOIN u.rol r " +
            "JOIN r.rolPermisos rp " +
            "JOIN rp.permiso p " +
            "WHERE u.UEmail = :email " +
            "AND u.UFechaBaja IS NULL " +
            "AND r.RFechaBaja IS NULL " +
            "AND rp.RPFechaHasta IS NULL " +
            "AND p.PCodigo = 'iniciar_sesion' " +
            "AND p.PFechaBaja IS NULL")
    Optional<Usuario> findByEmailActivoYConPermisoIniciarSesion(@Param("email") String email);
}
