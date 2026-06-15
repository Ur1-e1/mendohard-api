package com.mendohard.api.exception;


import lombok.Getter;

@Getter
public class IniciarSesionException extends RuntimeException {
    // Usamos el atributo exacto del modelo de datos
    private final Integer cantidad;

    public IniciarSesionException(String message) {
        super(message);
        this.cantidad = null;
    }

    public IniciarSesionException(String message, Integer cantidad) {
        super(message);
        this.cantidad = cantidad;
    }

    public IniciarSesionException(String message, Throwable cause) {
        super(message, cause);
        this.cantidad = null;
    }
}