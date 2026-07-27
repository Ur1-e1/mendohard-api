package com.mendohard.api.service.impl;

import com.mendohard.api.dto.RestablecerCredencialDto;
import com.mendohard.api.dto.SolicitarRecuperacionDto;
import com.mendohard.api.exception.ContrasennaNoCoincideException;
import com.mendohard.api.exception.DatosNoValidosException;
import com.mendohard.api.exception.OtpException;
import com.mendohard.api.model.Usuario;
import com.mendohard.api.otp.OtpInMemoryRepository;
import com.mendohard.api.otp.OtpRecord;
import com.mendohard.api.repository.UsuarioRepository;
import com.mendohard.api.service.EnviarCorreoOtpService;
import com.mendohard.api.service.RecuperarCredencialService;
import com.mendohard.api.service.strategy.ContrasenaEnSistemaStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * CU-05 — Implementación del caso de uso Recuperar Credencial.
 *
 * <p><b>Paso 1</b> ({@link #solicitarRecuperacion}): Genera y almacena un OTP efímero en RAM
 * y lo envía por email si el usuario existe. Siempre retorna el mismo mensaje (anti-enumeración).</p>
 *
 * <p><b>Paso 2</b> ({@link #restablecerCredencial}): Valida contraseñas, verifica el OTP
 * contra el registro en RAM y restablece la contraseña usando la estrategia de clave existente.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecuperarCredencialServiceImpl implements RecuperarCredencialService {

    private final UsuarioRepository usuarioRepository;
    private final OtpInMemoryRepository otpInMemoryRepository;
    private final EnviarCorreoOtpService enviarCorreoOtpService;
    private final ContrasenaEnSistemaStrategy contrasenaEnSistemaStrategy;

    // ─── Constantes de negocio ────────────────────────────────────────────────

    /** Mensaje estandarizado para UI 16 — idéntico sin importar si el usuario existe (anti-enumeración). */
    private static final String MSG_SOLICITUD_PROCESADA =
            "Si el correo electrónico ingresado corresponde a un usuario activo, se ha enviado un " +
            "código de seguridad. Revise su bandeja de entrada y ingrese los datos correspondientes";

    /** Mensaje estandarizado para UI 18 — OTP inválido, expirado o intentos agotados. */
    private static final String MSG_OTP_INVALIDO =
            "El código de verificación ingresado es incorrecto o ha expirado.";

    private static final int MAX_INTENTOS_OTP = 3;
    private static final int VIGENCIA_MINUTOS_OTP = 10;

    // ─────────────────────────────────────────────────────────────────────────
    // PASO 1 — POST /api/v1/auth/recuperar-credencial/solicitar
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public String solicitarRecuperacion(SolicitarRecuperacionDto dto) {
        String email = dto.getEmail().toLowerCase().trim();
        log.info("CU-05 Paso 1: Procesando solicitud de recuperación para email: {}", email);

        // Buscar usuario activo — la ausencia NO detiene el flujo (anti-enumeración CA N°2)
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUEmailAndUFechaBajaIsNull(email);

        if (usuarioOpt.isPresent()) {
            log.info("CU-05 Paso 1: Usuario activo encontrado — generando OTP.");

            String codigoOtp = generarOtp();

            OtpRecord record = OtpRecord.builder()
                    .codigoOTP(codigoOtp)
                    .fechaExpiracion(LocalDateTime.now().plusMinutes(VIGENCIA_MINUTOS_OTP))
                    .cantidadIntentos(0)
                    .build();

            otpInMemoryRepository.guardar(email, record);
            log.info("CU-05 Paso 1: OtpRecord almacenado en RAM para email: {}", email);

            // Enviar código OTP por email
            enviarCorreoOtpService.enviarCodigoOtp(email, codigoOtp);

        } else {
            // CA N°2 — Anti-enumeración: flujo silencioso, misma respuesta al Frontend
            log.info("CU-05 Paso 1 (Anti-enumeración CA N°2): Email no corresponde a usuario activo: {}", email);
        }

        // Respuesta idéntica en ambos casos → Frontend avanza a UI 16
        return MSG_SOLICITUD_PROCESADA;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PASO 2 — POST /api/v1/auth/recuperar-credencial/restablecer
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void restablecerCredencial(RestablecerCredencialDto dto) {
        String email = dto.getEmail().toLowerCase().trim();
        log.info("CU-05 Paso 2: Iniciando restablecimiento de credencial para email: {}", email);

        // CA N°7: Validar que la nueva contraseña coincide con la confirmación
        if (!dto.getNuevaContrasena().equals(dto.getConfirmacionContrasena())) {
            log.warn("CU-05 CA N°7: Contraseñas no coinciden para email: {}", email);
            throw new ContrasennaNoCoincideException(
                    "La nueva contraseña no coincide con la confirmación de contraseña.",
                    List.of("nuevaContrasena", "confirmacionContrasena")
            );
        }

        // CA N°3: Buscar OtpRecord en RAM — si no existe, el OTP es inválido o ya expiró
        OtpRecord record = otpInMemoryRepository.buscarPorEmail(email)
                .orElseThrow(() -> {
                    log.warn("CU-05 CA N°3: No se encontró OtpRecord en RAM para email: {}", email);
                    return new OtpException(MSG_OTP_INVALIDO);
                });

        // CA N°5: Validar que no se superó el límite de intentos previos
        if (record.getCantidadIntentos() >= MAX_INTENTOS_OTP) {
            log.warn("CU-05 CA N°5: Límite de {} intentos ya alcanzado para email: {}", MAX_INTENTOS_OTP, email);
            otpInMemoryRepository.eliminarPorEmail(email);
            throw new OtpException(MSG_OTP_INVALIDO);
        }

        // CA N°5: Validar que el OTP no haya expirado
        if (LocalDateTime.now().isAfter(record.getFechaExpiracion())) {
            log.warn("CU-05 CA N°5: OTP expirado para email: {}", email);
            otpInMemoryRepository.eliminarPorEmail(email);
            throw new OtpException(MSG_OTP_INVALIDO);
        }

        // CA N°6: Validar coincidencia del código OTP ingresado
        if (!dto.getCodigoIngresado().equals(record.getCodigoOTP())) {
            log.warn("CU-05 CA N°6: Código OTP incorrecto para email: {}", email);
            otpInMemoryRepository.incrementarIntentos(email);

            // Si tras el incremento se alcanzan los intentos máximos → limpiar registro de RAM
            otpInMemoryRepository.buscarPorEmail(email).ifPresent(actualizado -> {
                if (actualizado.getCantidadIntentos() >= MAX_INTENTOS_OTP) {
                    log.warn("CU-05 CA N°6: Intentos máximos alcanzados — eliminando OtpRecord para email: {}", email);
                    otpInMemoryRepository.eliminarPorEmail(email);
                }
            });

            throw new DatosNoValidosException("Datos ingresados no válidos.", List.of("codigoIngresado"));
        }

        // ─── CAMINO PRINCIPAL: código OTP correcto ────────────────────────────

        // Recuperar la entidad Usuario activa desde la BD
        Usuario usuario = usuarioRepository.findByUEmailAndUFechaBajaIsNull(email)
                .orElseThrow(() -> {
                    // Inconsistencia de datos: había OTP válido pero el usuario ya no existe activo
                    log.error("CU-05: Inconsistencia — OTP válido pero no se encontró usuario activo para email: {}", email);
                    return new OtpException(MSG_OTP_INVALIDO);
                });

        // Actualizar contraseña usando la estrategia de clave existente del proyecto
        contrasenaEnSistemaStrategy.modificarYGuardarClave(usuario, dto.getNuevaContrasena());
        log.info("CU-05: Contraseña restablecida exitosamente para usuario ID: {}", usuario.getId());

        // Eliminar OtpRecord de RAM para evitar reutilización del código
        otpInMemoryRepository.eliminarPorEmail(email);
        log.info("CU-05: OtpRecord eliminado de RAM para email: {} — flujo completado.", email);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper — Generación de OTP
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Genera un código OTP numérico de 6 dígitos (rango: 100000–999999).
     * El rango garantiza que siempre sean exactamente 6 dígitos sin relleno con ceros.
     */
    private String generarOtp() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }
}
