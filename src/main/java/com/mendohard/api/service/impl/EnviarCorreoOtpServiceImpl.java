package com.mendohard.api.service.impl;

import com.mendohard.api.service.EnviarCorreoOtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * CU-05 — Implementación del servicio de envío de código OTP por correo
 * electrónico.
 * Utiliza {@link JavaMailSender} configurado con las propiedades SMTP de
 * {@code application.properties}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnviarCorreoOtpServiceImpl implements EnviarCorreoOtpService {

    private final JavaMailSender mailSender;

    @Override
    public void enviarCodigoOtp(String destinatario, String codigoOtp) {
        log.info("CU-05: Preparando envío de código OTP al email: {}", destinatario);

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom("mendohard26@gmail.com");
        mensaje.setTo(destinatario);
        mensaje.setSubject("MendoHard — Código de seguridad para recuperar tu contraseña");
        mensaje.setText(
                "Hola,\n\n" +
                        "Recibimos una solicitud para recuperar la contraseña de tu cuenta en MendoHard.\n\n" +
                        "Tu código de verificación es:\n\n" +
                        "    " + codigoOtp + "\n\n" +
                        "Este código es válido por 10 minutos. No lo compartas con nadie.\n\n" +
                        "Si no solicitaste este código, podés ignorar este correo con seguridad.\n\n" +
                        "Saludos,\n" +
                        "El equipo de MendoHard");

        mailSender.send(mensaje);
        log.info("CU-05: Código OTP enviado exitosamente a: {}", destinatario);
    }
}
