package com.mendohard.api.otp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CU-05 — Repositorio en RAM para los registros OTP efímeros.
 *
 * <p>Utiliza un {@link ConcurrentHashMap} thread-safe indexado por el {@code email}
 * del usuario normalizado (lowercase + trim). Al ser un {@code @Component} singleton,
 * su ciclo de vida está ligado al de la aplicación Spring.</p>
 *
 * <p>Los registros son eliminados explícitamente tras el restablecimiento exitoso
 * o al alcanzar el límite de intentos / expiración.</p>
 */
@Component
@Slf4j
public class OtpInMemoryRepository {

    private final ConcurrentHashMap<String, OtpRecord> store = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────────────────────────────────────
    // Operaciones CRUD thread-safe
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Almacena (o sobreescribe) el {@link OtpRecord} asociado al email normalizado.
     * Si ya existía un registro previo para ese email, lo reemplaza.
     *
     * @param email  Email del usuario (se normaliza internamente a lowercase/trim)
     * @param record Registro OTP a almacenar
     */
    public void guardar(String email, OtpRecord record) {
        store.put(normalizar(email), record);
        log.debug("OtpInMemoryRepository: registro guardado para email normalizado '{}'", normalizar(email));
    }

    /**
     * Busca el {@link OtpRecord} asociado al email.
     *
     * @param email Email del usuario
     * @return {@link Optional} con el registro si existe, vacío en caso contrario
     */
    public Optional<OtpRecord> buscarPorEmail(String email) {
        return Optional.ofNullable(store.get(normalizar(email)));
    }

    /**
     * Incrementa en +1 el contador {@code cantidadIntentos} del registro asociado al email.
     * Operación atómica: si el registro no existe, no realiza ninguna acción.
     *
     * @param email Email del usuario
     */
    public void incrementarIntentos(String email) {
        store.computeIfPresent(normalizar(email), (clave, record) -> {
            record.setCantidadIntentos(record.getCantidadIntentos() + 1);
            log.debug("OtpInMemoryRepository: intentos incrementados a {} para '{}'",
                    record.getCantidadIntentos(), clave);
            return record;
        });
    }

    /**
     * Elimina el {@link OtpRecord} asociado al email, si existe.
     *
     * @param email Email del usuario
     */
    public void eliminarPorEmail(String email) {
        store.remove(normalizar(email));
        log.debug("OtpInMemoryRepository: registro eliminado para email normalizado '{}'", normalizar(email));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────────────────

    private String normalizar(String email) {
        return email.toLowerCase().trim();
    }
}
