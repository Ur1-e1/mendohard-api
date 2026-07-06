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
            "WHERE u.UEmail = :email " +
            "AND u.UFechaBaja IS NULL " +
            "AND r.RFechaBaja IS NULL " +
            "AND EXISTS (SELECT 1 FROM RolPermiso rp " +
            "            JOIN rp.permiso p " +
            "            WHERE rp MEMBER OF r.rolPermisos " + // 🔥 CORREGIDO: 'MEMBER OF' en vez de 'IN'
            "            AND p.PNombre = 'iniciar_sesion' " +
            "            AND p.PFechaBaja IS NULL " +
            "            AND rp.RPFechaHasta IS NULL)")
    Optional<Usuario> findByEmailActivoYConPermisoIniciarSesion(@Param("email") String email);

    // Precondición: Validar si el usuario en sesión tiene el permiso específico
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u " +
            "JOIN u.rol r " +
            "WHERE u.UEmail = :email " +
            "AND u.UFechaBaja IS NULL " +
            "AND r.RFechaBaja IS NULL " +
            "AND EXISTS (SELECT 1 FROM RolPermiso rp JOIN rp.permiso p " +
            "            WHERE rp MEMBER OF r.rolPermisos " +
            "            AND p.PNombre = 'registrar_responsablemendohard' " +
            "            AND p.PFechaBaja IS NULL " +
            "            AND rp.RPFechaHasta IS NULL)")
    boolean hasPermisoRegistrarResponsable(@Param("email") String email);

    // CA N°3: Validar si el correo ya está en uso
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Usuario u " +
            "WHERE u.UFechaBaja IS NULL AND u.UEmail = :email")
    boolean existsByUEmailActivo(@Param("email") String email);

    // CA N°3: Validar si el legajo ya está en uso (específico de la subclase)
    @Query("SELECT CASE WHEN COUNT(rmh) > 0 THEN true ELSE false END FROM ResponsableMendoHard rmh " +
            "WHERE rmh.UFechaBaja IS NULL AND rmh.RMHLegajo = :legajo")
    boolean existsByRMHLegajoActivo(@Param("legajo") String legajo);

}