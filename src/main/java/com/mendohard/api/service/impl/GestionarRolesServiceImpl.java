package com.mendohard.api.service.impl;

import com.mendohard.api.dto.AsignarPermisoRequestDTO;
import com.mendohard.api.dto.NavegacionOpcionDTO;
import com.mendohard.api.dto.PermisoResponseDTO;
import com.mendohard.api.dto.QuitarPermisoRequestDTO;
import com.mendohard.api.dto.RolResponseDTO;
import com.mendohard.api.exception.AsignacionExistenteException;
import com.mendohard.api.exception.DatosNoValidosException;
import com.mendohard.api.model.Permiso;
import com.mendohard.api.model.Rol;
import com.mendohard.api.model.RolPermiso;
import com.mendohard.api.repository.PermisoRepository;
import com.mendohard.api.repository.RolRepository;
import com.mendohard.api.service.GestionarRolesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * CU-06: Implementacion de la logica de negocio para Gestionar Roles.
 *
 * Nota de diseno:
 * - Las operaciones de asignar/quitar usan la coleccion en memoria del Rol
 * aprovechando CascadeType.ALL de la relacion Rol -> RolPermiso.
 * - La sanitizacion .trim() se aplica a todos los codigos recibidos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GestionarRolesServiceImpl implements GestionarRolesService {

        private final RolRepository rolRepository;
        private final PermisoRepository permisoRepository;

        // ─────────────────────────────────────────────────────────────────────────
        // 1. Obtener roles activos (UI 20 / UI 23)
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        public List<RolResponseDTO> obtenerRolesActivos() {
                log.info("[CU-06] Obteniendo todos los roles activos");

                return rolRepository.findAllByRFechaBajaIsNull()
                                .stream()
                                .map(rol -> new RolResponseDTO(
                                                rol.getRCodigo(),
                                                rol.getRNombre(),
                                                rol.getRDescripcion()))
                                .toList();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 2. Obtener permisos activos globales (UI 21)
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        public List<PermisoResponseDTO> obtenerPermisosActivosGlobales() {
                log.info("[CU-06] Obteniendo todos los permisos activos globales");

                return permisoRepository.findAllByPFechaBajaIsNull()
                                .stream()
                                .map(permiso -> new PermisoResponseDTO(
                                                permiso.getPCodigo(),
                                                permiso.getPNombre(),
                                                permiso.getPDescripcion()))
                                .toList();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 3. Obtener permisos activos por rol (UI 24)
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        @Transactional(readOnly = true)
        public List<PermisoResponseDTO> obtenerPermisosActivosPorRol(String rCodigo) {
                String codigoSanitizado = rCodigo.trim();
                log.info("[CU-06] Obteniendo permisos activos del rol: {}", codigoSanitizado);

                Rol rol = rolRepository.findByRCodigoAndRFechaBajaIsNull(codigoSanitizado)
                                .orElseThrow(() -> new DatosNoValidosException(
                                                "No se encontro un rol activo con el codigo: " + codigoSanitizado));

                if (rol.getRolPermisos() == null) {
                        log.warn("[CU-06] El rol {} no tiene lista de permisos asignados", codigoSanitizado);
                        return List.of();
                }

                return rol.getRolPermisos().stream()
                                .filter(rp -> rp.getRPFechaHasta() == null
                                                && rp.getPermiso() != null
                                                && rp.getPermiso().getPFechaBaja() == null)
                                .map(rp -> new PermisoResponseDTO(
                                                rp.getPermiso().getPCodigo(),
                                                rp.getPermiso().getPNombre(),
                                                rp.getPermiso().getPDescripcion()))
                                .toList();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 4. Asignar permiso a rol (UI 21 -> UI 22)
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        @Transactional
        public void asignarPermiso(AsignarPermisoRequestDTO dto) {
                String rCodigo = dto.RCodigo().trim();
                String pCodigo = dto.PCodigo().trim();
                log.info("[CU-06] Asignando permiso '{}' al rol '{}'", pCodigo, rCodigo);

                Rol rol = rolRepository.findByRCodigoAndRFechaBajaIsNull(rCodigo)
                                .orElseThrow(() -> new DatosNoValidosException(
                                                "No se encontro un rol activo con el codigo: " + rCodigo));

                Permiso permiso = permisoRepository.findByPCodigoAndPFechaBajaIsNull(pCodigo)
                                .orElseThrow(() -> new DatosNoValidosException(
                                                "No se encontro un permiso activo con el codigo: " + pCodigo));

                // CA N1: Verificar si ya existe la asignacion activa
                boolean asignacionExiste = rol.getRolPermisos().stream()
                                .anyMatch(rp -> rp.getRPFechaHasta() == null
                                                && rp.getPermiso() != null
                                                && rp.getPermiso().getPCodigo().equals(pCodigo));

                if (asignacionExiste) {
                        throw new AsignacionExistenteException("Ya existia esa asignacion creada");
                }

                // Camino principal: crear el nuevo RolPermiso y persistir via cascada
                RolPermiso nuevoRolPermiso = RolPermiso.builder()
                                .RPFechaDesde(LocalDate.now())
                                .RPFechaHasta(null)
                                .permiso(permiso)
                                .build();

                rol.getRolPermisos().add(nuevoRolPermiso);
                rolRepository.save(rol);

                log.info("[CU-06] Permiso '{}' asignado exitosamente al rol '{}'", pCodigo, rCodigo);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 5. Quitar permiso de rol (UI 24) - baja logica
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        @Transactional
        public void quitarPermiso(QuitarPermisoRequestDTO dto) {
                String rCodigo = dto.RCodigo().trim();
                String pCodigo = dto.PCodigo().trim();
                log.info("[CU-06] Quitando permiso '{}' del rol '{}'", pCodigo, rCodigo);

                Rol rol = rolRepository.findByRCodigoAndRFechaBajaIsNull(rCodigo)
                                .orElseThrow(() -> new DatosNoValidosException(
                                                "No se encontro un rol activo con el codigo: " + rCodigo));

                RolPermiso rolPermisoActivo = rol.getRolPermisos().stream()
                                .filter(rp -> rp.getRPFechaHasta() == null
                                                && rp.getPermiso() != null
                                                && rp.getPermiso().getPCodigo().equals(pCodigo))
                                .findFirst()
                                .orElseThrow(() -> new DatosNoValidosException(
                                                "No existe una asignacion activa entre el rol '" + rCodigo
                                                                + "' y el permiso '" + pCodigo + "'"));

                rolPermisoActivo.setRPFechaHasta(LocalDate.now());
                rolRepository.save(rol);

                log.info("[CU-06] Permiso '{}' quitado exitosamente del rol '{}'", pCodigo, rCodigo);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // 6. Procesar opcion de navegacion (UI 19)
        // ─────────────────────────────────────────────────────────────────────────

        @Override
        public NavegacionOpcionDTO procesarOpcionNavegacion(String opcionSeleccionada) {
                String opcion = opcionSeleccionada.trim();
                log.info("[CU-06] Procesando opcion de navegacion: '{}'", opcion);

                String mensaje = switch (opcion) {
                        case "ABM Rol" -> "Ir a ABM Rol";
                        case "ABM Permiso" -> "Ir a ABM Permiso";
                        case "Asignar Permiso" -> "Ir a Asignar Permiso";
                        case "Quitar Permiso" -> "Ir a Quitar Permiso";
                        default -> throw new DatosNoValidosException(
                                        "Opcion de navegacion no reconocida: " + opcion);
                };

                return new NavegacionOpcionDTO(opcion, mensaje);
        }
}