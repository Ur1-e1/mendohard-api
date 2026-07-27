package com.mendohard.api.controller;

import com.mendohard.api.dto.RestablecerCredencialDto;
import com.mendohard.api.dto.SolicitarRecuperacionDto;
import com.mendohard.api.service.RecuperarCredencialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * CU-05 Recuperar Credencial — Controller REST.
 *
 * <p>Rutas base: {@code /api/v1/auth/recuperar-credencial}
 *
 * <p><b>Seguridad:</b> Ambos endpoints son de acceso público ({@code permitAll()} en SecurityConfig).
 * El usuario no posee JWT al momento de ejecutar este flujo. La verificación de identidad
 * la provee el código OTP efímero almacenado en RAM.
 *
 * <p><b>Precondición de permiso:</b> La lógica de negocio del OTP garantiza que solo el
 * titular del correo electrónico pueda completar el restablecimiento.
 */
@RestController
@RequestMapping("/api/v1/auth/recuperar-credencial")
@RequiredArgsConstructor
@Slf4j
public class RecuperarCredencialController {

    private final RecuperarCredencialService recuperarCredencialService;

    // ─────────────────────────────────────────────────────────────────────────
    // PASO 1: POST /api/v1/auth/recuperar-credencial/solicitar
    // Genera y envía el código OTP → UI 16
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Recibe el email del usuario y, si corresponde a un usuario activo, genera y envía un OTP.
     * Siempre responde HTTP 200 con el mismo mensaje para prevenir enumeración de usuarios (UI 16).
     *
     * <p>CA N°1: Si el DTO no supera la validación {@code @Valid} (email vacío/inválido)
     * → {@code GlobalExceptionHandler} responde automáticamente con {@code DATA_INCONSISTENCY} (UI 03).
     *
     * @param dto DTO validado con el campo {@code email}
     * @return 200 OK con mensaje estandarizado anti-enumeración
     */
    @PostMapping("/solicitar")
    public ResponseEntity<Map<String, String>> solicitarRecuperacion(
            @Valid @RequestBody SolicitarRecuperacionDto dto) {

        log.info("POST /api/v1/auth/recuperar-credencial/solicitar — email: {}", dto.getEmail());

        String mensaje = recuperarCredencialService.solicitarRecuperacion(dto);

        return ResponseEntity.ok(Map.of("message", mensaje));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PASO 2: POST /api/v1/auth/recuperar-credencial/restablecer
    // Valida OTP y restablece la contraseña → UI 17
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Valida el código OTP ingresado, verifica coincidencia de contraseñas y restablece la credencial.
     *
     * <p>CA N°1: Si el DTO no supera la validación {@code @Valid} → {@code DATA_INCONSISTENCY} (UI 03).
     * <p>CA N°3: OTP no encontrado en RAM → {@code OTP_INVALID} (UI 18).
     * <p>CA N°5: OTP expirado o intentos agotados → {@code OTP_INVALID} (UI 18).
     * <p>CA N°6: Código OTP incorrecto → {@code DATA_INCONSISTENCY} (UI 03).
     * <p>CA N°7: Contraseñas no coinciden → {@code PASSWORD_MISMATCH} (UI 09).
     *
     * @param dto DTO validado con email, código OTP, nueva contraseña y confirmación
     * @return 200 OK con mensaje de éxito (UI 17)
     */
    @PostMapping("/restablecer")
    public ResponseEntity<Map<String, String>> restablecerCredencial(
            @Valid @RequestBody RestablecerCredencialDto dto) {

        log.info("POST /api/v1/auth/recuperar-credencial/restablecer — email: {}", dto.getEmail());

        recuperarCredencialService.restablecerCredencial(dto);

        return ResponseEntity.ok(Map.of("message", "La contraseña ha sido restablecida con éxito"));
    }
}
