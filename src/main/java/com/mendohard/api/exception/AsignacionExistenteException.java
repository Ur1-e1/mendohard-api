package com.mendohard.api.exception;

/**
 * CU-06 - CA N1: Lanzada cuando se intenta asignar un permiso a un rol
 * que ya posee esa asignacion activa (RPFechaHasta IS NULL).
 * Produce errorCode: "ASIGNACION_EXISTENTE" | HTTP 400.
 */
public class AsignacionExistenteException extends RuntimeException {

    public AsignacionExistenteException(String message) {
        super(message);
    }
}
