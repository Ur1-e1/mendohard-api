package com.mendohard.api.exception;

import lombok.Getter;

import java.util.List;

/**
 * CU-05 — CA N°6: Lanzada cuando el código OTP ingresado no coincide con el almacenado.
 * Produce errorCode: "DATA_INCONSISTENCY" | HTTP 400 (UI 03).
 * Sigue el mismo patrón estructural que las demás excepciones de negocio del proyecto.
 */
@Getter
public class DatosNoValidosException extends RuntimeException {

    private final List<String> invalidFields;

    public DatosNoValidosException(String message) {
        super(message);
        this.invalidFields = List.of();
    }

    public DatosNoValidosException(String message, List<String> invalidFields) {
        super(message);
        this.invalidFields = invalidFields != null ? invalidFields : List.of();
    }
}
