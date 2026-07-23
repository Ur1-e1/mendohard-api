package com.mendohard.api.exception;


import lombok.Getter;

import java.util.List;

@Getter
public class IniciarSesionException extends RuntimeException {

    // Usamos el atributo exacto del modelo de datos
    private final Integer cantidad;
    private final List<String> invalidFields;

    public IniciarSesionException(String message) {
        super(message);
        this.cantidad = null;
        this.invalidFields = List.of();
    }

    public IniciarSesionException(String message, Integer cantidad) {
        super(message);
        this.cantidad = cantidad;
        this.invalidFields = List.of();
    }

    public IniciarSesionException(String message, List<String> invalidFields, Integer cantidad) {
        super(message);
        this.invalidFields = invalidFields != null ? invalidFields : List.of();
        this.cantidad = cantidad;
    }

    public IniciarSesionException(String message, Throwable cause) {
        super(message, cause);
        this.cantidad = null;
        this.invalidFields = List.of();
    }
}