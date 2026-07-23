package com.mendohard.api.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class UsuarioYaExisteException extends RuntimeException {

    private final List<String> invalidFields;

    public UsuarioYaExisteException(String message) {
        super(message);
        this.invalidFields = List.of();
    }

    public UsuarioYaExisteException(String message, List<String> invalidFields) {
        super(message);
        this.invalidFields = invalidFields != null ? invalidFields : List.of();
    }
}
