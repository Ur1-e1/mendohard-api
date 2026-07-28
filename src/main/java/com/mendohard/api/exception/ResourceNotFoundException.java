package com.mendohard.api.exception;

import lombok.Getter;

import java.util.List;

/**
 * Excepción lanzada cuando un recurso solicitado no se encuentra en el sistema.
 * Produce errorCode: "RESOURCE_NOT_FOUND" | HTTP 404.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final List<String> invalidFields;

    public ResourceNotFoundException(String message) {
        super(message);
        this.invalidFields = List.of();
    }

    public ResourceNotFoundException(String message, List<String> invalidFields) {
        super(message);
        this.invalidFields = invalidFields != null ? invalidFields : List.of();
    }
}
