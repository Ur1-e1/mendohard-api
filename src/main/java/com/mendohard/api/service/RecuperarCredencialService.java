package com.mendohard.api.service;

import com.mendohard.api.dto.RestablecerCredencialDto;
import com.mendohard.api.dto.SolicitarRecuperacionDto;

/**
 * CU-05 — Contrato de servicio para el caso de uso Recuperar Credencial.
 * La autenticación NO es requerida para estos endpoints (usuario sin JWT activo).
 * La seguridad del flujo la provee el OTP efímero almacenado en RAM.
 */
public interface RecuperarCredencialService {

    /**
     * Paso 1: Genera y envía un código OTP al email indicado si corresponde a un usuario activo.
     * Implementa comportamiento anti-enumeración: siempre retorna el mismo mensaje HTTP 200.
     *
     * @param dto DTO con el email del solicitante
     * @return Mensaje estandarizado para UI 16 (idéntico independientemente de si el usuario existe)
     */
    String solicitarRecuperacion(SolicitarRecuperacionDto dto);

    /**
     * Paso 2: Valida el código OTP, verifica coincidencia de contraseñas y restablece la credencial.
     * Lanza excepciones específicas para cada camino alternativo (UI 03, UI 09, UI 18).
     *
     * @param dto DTO con email, código OTP, nueva contraseña y confirmación
     */
    void restablecerCredencial(RestablecerCredencialDto dto);
}
