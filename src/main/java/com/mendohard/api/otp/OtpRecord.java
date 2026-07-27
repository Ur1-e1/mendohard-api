package com.mendohard.api.otp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CU-05 — Registro efímero de OTP almacenado en RAM.
 * No es una entidad JPA — vive únicamente en el {@link OtpInMemoryRepository}.
 *
 * <ul>
 *   <li>{@code codigoOTP}:        Código aleatorio de 6 dígitos numéricos.</li>
 *   <li>{@code fechaExpiracion}:  {@code LocalDateTime.now().plusMinutes(10)} al momento de creación.</li>
 *   <li>{@code cantidadIntentos}: Contador de intentos fallidos, inicializado en 0.</li>
 * </ul>
 */
@Data
@AllArgsConstructor
@Builder
public class OtpRecord {

    private String codigoOTP;
    private LocalDateTime fechaExpiracion;
    private int cantidadIntentos;
}
