package com.mendohard.api.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ContrasennaNoCoincideException extends RuntimeException {

    private final List<String> invalidFields;

    public ContrasennaNoCoincideException(String message) {
        super(message);
        this.invalidFields = List.of();
    }

    public ContrasennaNoCoincideException(String message, List<String> invalidFields) {
        super(message);
        this.invalidFields = invalidFields != null ? invalidFields : List.of();
    }
}
