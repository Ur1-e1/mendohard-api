package com.mendohard.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maneja las denegaciones de acceso que ocurren DENTRO de la cadena de filtros de Spring Security,
 * antes de que la excepción pueda llegar al {@code @RestControllerAdvice}.
 *
 * <p>Escenarios cubiertos:
 * <ul>
 *   <li>Token JWT válido pero rol insuficiente para la URL (reglas en {@code SecurityFilterChain})</li>
 *   <li>Cualquier {@code AccessDeniedException} disparada en el nivel de filtro</li>
 * </ul>
 *
 * <p>La respuesta generada es idéntica en estructura al DTO de error unificado del proyecto
 * (construido con el mismo patrón que el {@code buildResponse} de {@code GlobalExceptionHandler}).
 */
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        log.warn("CustomAccessDeniedHandler: acceso denegado en '{}' — {}",
                request.getRequestURI(), accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Construimos el mismo mapa que usa GlobalExceptionHandler.buildResponse(...)
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("errorCode", "ACCESS_DENIED");
        errorBody.put("message", "Acceso denegado: No posee los permisos o roles requeridos para realizar esta acción.");
        errorBody.put("status", 403);
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("invalidFields", List.of());

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
