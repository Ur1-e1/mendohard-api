package com.mendohard.api.service.impl;

import com.mendohard.api.dto.ModificarPerfilConsumidorRequestDTO;
import com.mendohard.api.dto.ModificarPerfilVendedorRequestDTO;
import com.mendohard.api.dto.PerfilConsumidorResponseDTO;
import com.mendohard.api.dto.PerfilVendedorResponseDTO;
import com.mendohard.api.exception.RegistroException;
import com.mendohard.api.exception.UsuarioYaExisteException;
import com.mendohard.api.model.Consumidor;
import com.mendohard.api.model.Usuario;
import com.mendohard.api.model.Vendedor;
import com.mendohard.api.repository.ConsumidorRepository;
import com.mendohard.api.repository.UsuarioRepository;
import com.mendohard.api.repository.VendedorRepository;
import com.mendohard.api.service.ModificarPerfilService;
import com.mendohard.api.service.factory.ClaveStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModificarPerfilServiceImpl implements ModificarPerfilService {

    private final UsuarioRepository usuarioRepository;
    private final ConsumidorRepository consumidorRepository;
    private final VendedorRepository vendedorRepository;
    private final ClaveStrategyFactory claveStrategyFactory;

    // ─────────────────────────────────────────────────────────────────────────
    // A. Obtención de Perfil Actual — GET /api/perfil/me
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Object obtenerPerfilActual(String email) {
        log.info("CU-04: Obteniendo perfil actual para email: {}", email);

        Usuario usuario = usuarioRepository.findByUEmailAndUFechaBajaIsNull(email)
                .orElseThrow(() -> new RegistroException(
                        "No se encontró usuario activo con email: " + email));

        if (usuario instanceof Consumidor consumidor) {
            log.info("CU-04: Perfil identificado como Consumidor — ID: {}", consumidor.getId());
            return new PerfilConsumidorResponseDTO(
                    consumidor.getCApodo(),
                    consumidor.getUNombre(),
                    consumidor.getUApellido(),
                    consumidor.getUEmail());
        }

        if (usuario instanceof Vendedor vendedor) {
            log.info("CU-04: Perfil identificado como Vendedor — ID: {}", vendedor.getId());
            return new PerfilVendedorResponseDTO(
                    vendedor.getVTelefono(),
                    vendedor.getUNombre(),
                    vendedor.getUApellido(),
                    vendedor.getUEmail());
        }

        // Si la instancia no es Consumidor ni Vendedor (ej: ResponsableMendoHard), no
        // aplica CU-04
        throw new RegistroException(
                "El tipo de usuario no soporta la operación de modificar perfil: "
                        + usuario.getClass().getSimpleName());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // B. Modificación de Perfil Consumidor — PUT /api/perfil/consumidor (UI 13)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void modificarPerfilConsumidor(String email, ModificarPerfilConsumidorRequestDTO request) {
        log.info("CU-04: Iniciando modificación de perfil Consumidor para email: {}", email);

        // Recuperar el consumidor activo en sesión
        Consumidor consumidor = consumidorRepository.findByEmailActivo(email)
                .orElseThrow(() -> new RegistroException(
                        "No se encontró consumidor activo con email: " + email));

        // Validación de unicidad de Email (CA N°2 — si cambió)
        if (!consumidor.getUEmail().equalsIgnoreCase(request.uemail())) {
            if (usuarioRepository.existsByUEmailAndUFechaBajaIsNullAndIdNot(request.uemail(), consumidor.getId())) {
                log.warn("CU-04: Email '{}' ya existe en otro usuario activo", request.uemail());
                throw new UsuarioYaExisteException(
                        "Ya existe un usuario con el email ingresado",
                        List.of("uemail"));
            }
        }

        // Validación de unicidad de Apodo (CA N°8 — si cambió)
        if (!consumidor.getCApodo().equalsIgnoreCase(request.capodo())) {
            if (consumidorRepository.existsByCApodoAndUFechaBajaIsNullAndIdNot(request.capodo(), consumidor.getId())) {
                log.warn("CU-04: Apodo '{}' ya existe en otro consumidor activo", request.capodo());
                throw new UsuarioYaExisteException(
                        "Ya existe un consumidor con ese apodo",
                        List.of("capodo"));
            }
        }

        // Actualización de datos del consumidor
        consumidor.setCApodo(request.capodo());
        consumidor.setUNombre(request.unombre());
        consumidor.setUApellido(request.uapellido());
        consumidor.setUEmail(request.uemail());
        consumidorRepository.save(consumidor);
        log.info("CU-04: Datos de consumidor ID {} actualizados correctamente", consumidor.getId());

        // Camino Alternativo N°2: tratamiento condicional de nueva contraseña
        if (request.nuevaContrasenna() != null && !request.nuevaContrasenna().isBlank()) {
            log.info("CU-04 CA N°2: Actualizando contraseña para consumidor ID: {}", consumidor.getId());
            claveStrategyFactory
                    .getStrategy(consumidor.getAlgoritmoClave().getACNombre())
                    .modificarYGuardarClave(consumidor, request.nuevaContrasenna());
        }

        log.info("CU-04: Modificación de perfil Consumidor ID {} completada", consumidor.getId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // C. Modificación de Perfil Vendedor — PUT /api/perfil/vendedor (UI 14)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void modificarPerfilVendedor(String email, ModificarPerfilVendedorRequestDTO request) {
        log.info("CU-04: Iniciando modificación de perfil Vendedor para email: {}", email);

        // Recuperar el vendedor activo en sesión
        Vendedor vendedor = vendedorRepository.findByEmailActivo(email)
                .orElseThrow(() -> new RegistroException(
                        "No se encontró vendedor activo con email: " + email));

        // Validación de unicidad de Email (CA N°5 — si cambió)
        if (!vendedor.getUEmail().equalsIgnoreCase(request.uemail())) {
            if (usuarioRepository.existsByUEmailAndUFechaBajaIsNullAndIdNot(request.uemail(), vendedor.getId())) {
                log.warn("CU-04: Email '{}' ya existe en otro usuario activo", request.uemail());
                throw new UsuarioYaExisteException(
                        "Ya existe un usuario con el email ingresado",
                        List.of("uemail"));
            }
        }

        // Actualización de datos del vendedor
        vendedor.setVTelefono(request.vtelefono());
        vendedor.setUNombre(request.unombre());
        vendedor.setUApellido(request.uapellido());
        vendedor.setUEmail(request.uemail());
        vendedorRepository.save(vendedor);
        log.info("CU-04: Datos de vendedor ID {} actualizados correctamente", vendedor.getId());

        // Camino Alternativo N°6: tratamiento condicional de nueva contraseña
        if (request.nuevaContrasenna() != null && !request.nuevaContrasenna().isBlank()) {
            log.info("CU-04 CA N°6: Actualizando contraseña para vendedor ID: {}", vendedor.getId());
            claveStrategyFactory
                    .getStrategy(vendedor.getAlgoritmoClave().getACNombre())
                    .modificarYGuardarClave(vendedor, request.nuevaContrasenna());
        }

        log.info("CU-04: Modificación de perfil Vendedor ID {} completada", vendedor.getId());
    }
}
