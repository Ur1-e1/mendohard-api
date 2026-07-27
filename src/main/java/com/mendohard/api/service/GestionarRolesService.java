package com.mendohard.api.service;

import com.mendohard.api.dto.AsignarPermisoRequestDTO;
import com.mendohard.api.dto.NavegacionOpcionDTO;
import com.mendohard.api.dto.PermisoResponseDTO;
import com.mendohard.api.dto.QuitarPermisoRequestDTO;
import com.mendohard.api.dto.RolResponseDTO;

import java.util.List;

/**
 * CU-06: Gestionar Roles.
 * Define las operaciones de negocio para la gestion de roles y sus permisos.
 */
public interface GestionarRolesService {

    /**
     * Retorna todos los roles con RFechaBaja nula.
     * Usado en UI 20 (listar roles) y UI 23 (seleccionar rol).
     */
    List<RolResponseDTO> obtenerRolesActivos();

    /**
     * Retorna todos los permisos con PFechaBaja nula.
     * Usado en UI 21 (seleccionar permiso para asignar).
     */
    List<PermisoResponseDTO> obtenerPermisosActivosGlobales();

    /**
     * Retorna los permisos activos actualmente asignados a un rol especifico.
     * Filtra RolPermiso con RPFechaHasta IS NULL y permiso con PFechaBaja IS NULL.
     * Usado en UI 24 (gestionar permisos del rol seleccionado).
     *
     * @param rCodigo Codigo del rol (se sanitiza con trim).
     */
    List<PermisoResponseDTO> obtenerPermisosActivosPorRol(String rCodigo);

    /**
     * Asigna un permiso a un rol creando un nuevo registro RolPermiso activo.
     * CA N1: lanza AsignacionExistenteException si ya existe la relacion activa.
     *
     * @param dto Contiene RCodigo y PCodigo (sanitizados con trim).
     */
    void asignarPermiso(AsignarPermisoRequestDTO dto);

    /**
     * Realiza la baja logica de una asignacion activa (RPFechaHasta = hoy).
     * CA N2: lanza DatosNoValidosException si la relacion activa no existe.
     *
     * @param dto Contiene RCodigo y PCodigo (sanitizados con trim).
     */
    void quitarPermiso(QuitarPermisoRequestDTO dto);

    /**
     * Procesa la opcion de navegacion seleccionada en el menu principal (UI 19).
     *
     * @param opcionSeleccionada Opcion elegida (sanitizada con trim).
     */
    NavegacionOpcionDTO procesarOpcionNavegacion(String opcionSeleccionada);
}