package com.mendohard.api.exception;

/**
 * CU-05 — CA N°3 / CA N°5: Lanzada cuando el OtpRecord no existe en RAM,
 * el código ha expirado o se han alcanzado los intentos máximos.
 * Produce errorCode: "OTP_INVALID" | HTTP 400 (UI 18).
 */
public class OtpException extends RuntimeException {

    public OtpException(String message) {
        super(message);
    }
}
