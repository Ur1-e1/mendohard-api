package com.mendohard.api.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ─────────────────────────────────────────────────────────────────────────
    // A. Inconsistencia / Validación Fallida de Datos de Entrada → UI 03
    //    errorCode: DATA_INCONSISTENCY | status: 400
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: validación de campos de entrada fallida");

        List<String> invalidFields = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField())
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildResponse("DATA_INCONSISTENCY", "Datos ingresados no válidos", 400, invalidFields, null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // B. Contraseña no coincide con Confirmación → UI 09
    //    errorCode: PASSWORD_MISMATCH | status: 400
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(ContrasennaNoCoincideException.class)
    public ResponseEntity<Map<String, Object>> handleContrasennaNoCoincideException(ContrasennaNoCoincideException ex) {
        log.error("ContrasennaNoCoincideException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildResponse("PASSWORD_MISMATCH", "La contraseña no coincide con la confirmación", 400, ex.getInvalidFields(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // C. Usuario o Entidad Ya Existente → UI 10
    //    errorCode: USER_ALREADY_EXISTS | status: 400
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(UsuarioYaExisteException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioYaExisteException(UsuarioYaExisteException ex) {
        log.error("UsuarioYaExisteException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildResponse("USER_ALREADY_EXISTS", "Ya existe un usuario/comercio registrado con los datos ingresados", 400, ex.getInvalidFields(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // D. Credencial/Contraseña Incorrecta en Login → UI 04
    //    errorCode: INVALID_CREDENTIALS | status: 401
    //    extraData: {"cantidad": N}
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(IniciarSesionException.class)
    public ResponseEntity<Map<String, Object>> handleIniciarSesionException(IniciarSesionException ex) {
        log.error("IniciarSesionException: {}", ex.getMessage());

        Map<String, Object> extraData = null;
        if (ex.getCantidad() != null) {
            extraData = new HashMap<>();
            extraData.put("cantidad", ex.getCantidad());
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(buildResponse("INVALID_CREDENTIALS", "Contraseña no válida", 401, ex.getInvalidFields(), extraData));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // E. Intentos Máximos Alcanzados en Login → UI 04 / Bloqueo
    //    errorCode: MAX_LOGIN_ATTEMPTS_REACHED | status: 400
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(IntentosMaximosException.class)
    public ResponseEntity<Map<String, Object>> handleIntentosMaximosException(IntentosMaximosException ex) {
        log.error("IntentosMaximosException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildResponse("MAX_LOGIN_ATTEMPTS_REACHED", "Intentos máximos de iniciar sesión alcanzados", 400, List.of(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // F. Acceso Denegado / Sin Permisos
    //    errorCode: ACCESS_DENIED | status: 403
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(AccesoDenegadoException.class)
    public ResponseEntity<Map<String, Object>> handleAccesoDenegadoException(AccesoDenegadoException ex) {
        log.error("AccesoDenegadoException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildResponse("ACCESS_DENIED", "No posee permisos para ejecutar esta acción", 403, List.of(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // F2. Acceso Denegado por Spring Security (AccessDeniedException)
    //     Cubre: @PreAuthorize fallido, hasRole/hasAnyRole en reglas de URL
    //     cuando la excepción escala hasta el ControllerAdvice.
    //     errorCode: ACCESS_DENIED | status: 403
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleSpringAccessDeniedException(AccessDeniedException ex) {
        log.warn("AccessDeniedException (Spring Security): {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(buildResponse("ACCESS_DENIED",
                        "Acceso denegado: No posee los permisos o roles requeridos para realizar esta acción.",
                        403, List.of(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // H. CU-05 — Código OTP incorrecto → UI 03
    //    errorCode: DATA_INCONSISTENCY | status: 400
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(DatosNoValidosException.class)
    public ResponseEntity<Map<String, Object>> handleDatosNoValidosException(DatosNoValidosException ex) {
        log.error("DatosNoValidosException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildResponse("DATA_INCONSISTENCY", "Datos ingresados no válidos", 400, ex.getInvalidFields(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // I. CU-05 — Código OTP inexistente, expirado o intentos agotados → UI 18
    //    errorCode: OTP_INVALID | status: 400
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(OtpException.class)
    public ResponseEntity<Map<String, Object>> handleOtpException(OtpException ex) {
        log.error("OtpException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildResponse("OTP_INVALID", ex.getMessage(), 400, List.of(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // J. CU-06 — Asignación RolPermiso ya existente → CA N°1
    //    errorCode: ASIGNACION_EXISTENTE | status: 400
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(AsignacionExistenteException.class)
    public ResponseEntity<Map<String, Object>> handleAsignacionExistenteException(AsignacionExistenteException ex) {
        log.error("AsignacionExistenteException: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildResponse("ASIGNACION_EXISTENTE", ex.getMessage(), 400, List.of(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // G. Errores de sistema/infraestructura no controlados
    //    errorCode: INTERNAL_SERVER_ERROR | status: 500
    // ─────────────────────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        log.error("Error no controlado", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildResponse("INTERNAL_SERVER_ERROR", "Error interno del servidor", 500, List.of(), null));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper: construye el payload de error estandarizado
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Construye la estructura JSON de respuesta de error unificada.
     *
     * @param errorCode     Identificador técnico único para el Frontend (ej: "DATA_INCONSISTENCY")
     * @param message       Mensaje técnico/log en español
     * @param status        Código HTTP numérico
     * @param invalidFields Lista de nombres de campos DTO que fallaron (vacía si no aplica)
     * @param extraData     Datos complementarios de negocio (ej: {"cantidad": 3}); null si no aplica
     * @return Mapa con la estructura estándar de error
     */
    private Map<String, Object> buildResponse(String errorCode,
                                               String message,
                                               int status,
                                               List<String> invalidFields,
                                               Map<String, Object> extraData) {
        Map<String, Object> response = new HashMap<>();
        response.put("errorCode", errorCode);
        response.put("message", message);
        response.put("status", status);
        response.put("timestamp", LocalDateTime.now());
        response.put("invalidFields", invalidFields != null ? invalidFields : List.of());

        if (extraData != null && !extraData.isEmpty()) {
            response.put("extraData", extraData);
        }

        return response;
    }
}