package com.mendohard.api.service;

import com.mendohard.api.dto.ModificarPerfilConsumidorRequestDTO;
import com.mendohard.api.dto.ModificarPerfilVendedorRequestDTO;

/**
 * Contrato de servicio para el caso de uso CU-04 Modificar Perfil.
 * La autenticación y autorización son responsabilidad de la capa de seguridad (Spring Security / JWT).
 */
public interface ModificarPerfilService {

    /**
     * Obtiene el perfil actual del usuario autenticado.
     * Retorna {@link com.mendohard.api.dto.PerfilConsumidorResponseDTO} si es Consumidor,
     * o {@link com.mendohard.api.dto.PerfilVendedorResponseDTO} si es Vendedor.
     *
     * @param email Email extraído del SecurityContext (principal del JWT)
     * @return DTO de perfil tipado según la instancia del usuario
     */
    Object obtenerPerfilActual(String email);

    /**
     * Modifica el perfil de un Consumidor autenticado (UI 13).
     * Valida unicidad de email y apodo antes de persistir.
     * Camino Alternativo N°2: Si {@code nuevaContrasenna} no es blank, actualiza la clave.
     *
     * @param email   Email del consumidor en sesión (extraído del JWT)
     * @param request DTO con los nuevos datos del perfil
     */
    void modificarPerfilConsumidor(String email, ModificarPerfilConsumidorRequestDTO request);

    /**
     * Modifica el perfil de un Vendedor autenticado (UI 14).
     * Valida unicidad de email antes de persistir.
     * Camino Alternativo N°6: Si {@code nuevaContrasenna} no es blank, actualiza la clave.
     *
     * @param email   Email del vendedor en sesión (extraído del JWT)
     * @param request DTO con los nuevos datos del perfil
     */
    void modificarPerfilVendedor(String email, ModificarPerfilVendedorRequestDTO request);
}
