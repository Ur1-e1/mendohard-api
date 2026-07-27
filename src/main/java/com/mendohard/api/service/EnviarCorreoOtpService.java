package com.mendohard.api.service;

/**
 * CU-05 — Contrato de servicio para el envío del código OTP por correo electrónico.
 * La implementación utiliza {@code JavaMailSender} con la configuración SMTP
 * definida en {@code application.properties}.
 */
public interface EnviarCorreoOtpService {

    /**
     * Envía el código OTP al correo electrónico del usuario.
     *
     * @param destinatario Email del usuario que solicitó la recuperación
     * @param codigoOtp    Código de 6 dígitos generado en RAM
     */
    void enviarCodigoOtp(String destinatario, String codigoOtp);
}
