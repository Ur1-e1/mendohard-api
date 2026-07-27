package com.mendohard.api.repository;

import com.mendohard.api.model.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    /**
     * CU-06: Verifica si ya existe una asignacion activa (RPFechaHasta IS NULL)
     * entre un rol y un permiso, dado el codigo de cada uno.
     * Usa JPQL explicito porque RolPermiso no posee campo 'rol' directo;
     * el join se realiza desde la coleccion rolPermisos de la entidad Rol.
     */
    @Query("SELECT rp FROM Rol r JOIN r.rolPermisos rp " +
           "WHERE r.RCodigo = :rCodigo " +
           "AND rp.permiso.PCodigo = :pCodigo " +
           "AND rp.RPFechaHasta IS NULL")
    Optional<RolPermiso> findByRolCodigoAndPermisoCodigoActivo(
            @Param("rCodigo") String rCodigo,
            @Param("pCodigo") String pCodigo
    );

    /**
     * CU-06: Devuelve todos los permisos activos asignados a un rol especifico.
     * Filtra: RPFechaHasta IS NULL y el permiso asociado tenga PFechaBaja IS NULL.
     */
    @Query("SELECT rp FROM Rol r JOIN r.rolPermisos rp " +
           "WHERE r.RCodigo = :rCodigo " +
           "AND rp.RPFechaHasta IS NULL " +
           "AND rp.permiso.PFechaBaja IS NULL")
    List<RolPermiso> findAllActivosByRolCodigo(@Param("rCodigo") String rCodigo);
}