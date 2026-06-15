package com.mendohard.api.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IniciarSesionException.class)
    public ResponseEntity<Map<String, Object>> handleIniciarSesionException(IniciarSesionException ex) {
        log.error("IniciarSesionException: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("codigo", 401);

        // Si la excepción transporta el valor de IFCantidad, lo mapeamos directamente al JSON
        if (ex.getCantidad() != null) {
            response.put("cantidad", ex.getCantidad());
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(IntentosMaximosException.class)
    public ResponseEntity<Map<String, Object>> handleIntentosMaximosException(IntentosMaximosException ex) {
        log.error("IntentosMaximosException: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", ex.getMessage());
        response.put("timestamp", LocalDateTime.now());
        response.put("codigo", 400);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation error");

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Validación fallida en los parámetros de entrada");
        response.put("timestamp", LocalDateTime.now());
        response.put("codigo", 400);
        response.put("detalles", ex.getBindingResult().getFieldError().getDefaultMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        log.error("Error no controlado", ex);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Error interno del servidor");
        response.put("timestamp", LocalDateTime.now());
        response.put("codigo", 500);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}