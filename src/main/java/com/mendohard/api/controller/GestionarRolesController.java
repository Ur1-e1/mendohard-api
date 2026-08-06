package com.mendohard.api.controller;

import com.mendohard.api.dto.AsignarPermisoRequestDTO;
import com.mendohard.api.dto.NavegacionOpcionDTO;
import com.mendohard.api.dto.OpcionNavegacionRequestDTO;
import com.mendohard.api.dto.PermisoResponseDTO;
import com.mendohard.api.dto.QuitarPermisoRequestDTO;
import com.mendohard.api.dto.RolResponseDTO;
import com.mendohard.api.service.GestionarRolesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * CU-06: Gestionar Roles.
 * Expone los endpoints REST para la gestion de roles y permisos del sistema.
 *
 * Seguridad: protegido a nivel de URL en SecurityConfig
 * (.requestMatchers("/api/v1/gestionar-roles/**").hasRole("Responsable
 * MendoHard")).
 * El acceso no autorizado es manejado por CustomAccessDeniedHandler (HTTP 403).
 */
@RestController
@RequestMapping("/api/v1/gestionar-roles")
@RequiredArgsConstructor
@Slf4j
public class GestionarRolesController {

        private final GestionarRolesService gestionarRolesService;

        // ─────────────────────────────────────────────────────────────────────────
        // GET /roles-activos
        // Retorna la lista de roles con RFechaBaja nula (UI 20 / UI 23).
        // ─────────────────────────────────────────────────────────────────────────

        @GetMapping("/roles-activos")
        public ResponseEntity<List<RolResponseDTO>> obtenerRolesActivos() {
                log.info("GET /api/v1/gestionar-roles/roles-activos — Solicitud recibida");

                List<RolResponseDTO> roles = gestionarRolesService.obtenerRolesActivos();

                return ResponseEntity.status(HttpStatus.OK).body(roles);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // GET /permisos-activos
        // Retorna la lista global de permisos con PFechaBaja nula (UI 21).
        // ─────────────────────────────────────────────────────────────────────────

        @GetMapping("/permisos-activos")
        public ResponseEntity<List<PermisoResponseDTO>> obtenerPermisosActivos() {
                log.info("GET /api/v1/gestionar-roles/permisos-activos — Solicitud recibida");

                List<PermisoResponseDTO> permisos = gestionarRolesService.obtenerPermisosActivosGlobales();

                return ResponseEntity.status(HttpStatus.OK).body(permisos);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // GET /roles/{rCodigo}/permisos
        // Retorna los permisos activos asignados al rol indicado (UI 24).
        // ─────────────────────────────────────────────────────────────────────────

        @GetMapping("/roles/{rCodigo}/permisos")
        public ResponseEntity<List<PermisoResponseDTO>> obtenerPermisosDelRol(
                        @PathVariable("rCodigo") String rCodigo) {

                log.info("GET /api/v1/gestionar-roles/roles/{}/permisos — Solicitud recibida", rCodigo);

                List<PermisoResponseDTO> permisos = gestionarRolesService.obtenerPermisosActivosPorRol(rCodigo);

                return ResponseEntity.status(HttpStatus.OK).body(permisos);
        }

        // ─────────────────────────────────────────────────────────────────────────
        // POST /asignar-permiso
        // Vincula un permiso a un rol creando un RolPermiso activo (UI 21 -> UI 22).
        // ─────────────────────────────────────────────────────────────────────────

        @PostMapping("/asignar-permiso")
        public ResponseEntity<Map<String, String>> asignarPermiso(
                        @Valid @RequestBody AsignarPermisoRequestDTO request) {

                log.info("POST /api/v1/gestionar-roles/asignar-permiso — RCodigo: {}, PCodigo: {}",
                                request.RCodigo(), request.PCodigo());

                gestionarRolesService.asignarPermiso(request);

                return ResponseEntity.status(HttpStatus.OK)
                                .body(Map.of("mensaje", "Permiso asignado con exito"));
        }

        // ─────────────────────────────────────────────────────────────────────────
        // POST /quitar-permiso
        // Realiza la baja logica del RolPermiso asignando RPFechaHasta (UI 24).
        // ─────────────────────────────────────────────────────────────────────────

        @PostMapping("/quitar-permiso")
        public ResponseEntity<Map<String, String>> quitarPermiso(
                        @Valid @RequestBody QuitarPermisoRequestDTO request) {

                log.info("POST /api/v1/gestionar-roles/quitar-permiso — RCodigo: {}, PCodigo: {}",
                                request.RCodigo(), request.PCodigo());

                gestionarRolesService.quitarPermiso(request);

                return ResponseEntity.status(HttpStatus.OK)
                                .body(Map.of("mensaje", "Permiso quitado con exito"));
        }

        // ─────────────────────────────────────────────────────────────────────────
        // POST /opcion-navegacion
        // Procesa la opcion seleccionada en el menu principal (UI 19).
        // ─────────────────────────────────────────────────────────────────────────

        @PostMapping("/opcion-navegacion")
        public ResponseEntity<NavegacionOpcionDTO> procesarOpcionNavegacion(
                        @Valid @RequestBody OpcionNavegacionRequestDTO request) {

                log.info("POST /api/v1/gestionar-roles/opcion-navegacion — opcion: {}",
                                request.opcionSeleccionada());

                NavegacionOpcionDTO respuesta = gestionarRolesService
                                .procesarOpcionNavegacion(request.opcionSeleccionada());

                return ResponseEntity.status(HttpStatus.OK).body(respuesta);
        }
}