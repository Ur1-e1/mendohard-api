package com.mendohard.api.service.impl;


import com.mendohard.api.dto.IniciarSesionRequestDTO;
import com.mendohard.api.dto.IniciarSesionResponseDTO;
import com.mendohard.api.exception.IniciarSesionException;
import com.mendohard.api.exception.IntentosMaximosException;
import com.mendohard.api.model.Clave;
import com.mendohard.api.model.IntentoFallido;
import com.mendohard.api.model.Usuario;
import com.mendohard.api.repository.ClaveRepository;
import com.mendohard.api.repository.IntentoFallidoRepository;
import com.mendohard.api.repository.UsuarioRepository;
import com.mendohard.api.repository.VendedorEstadoRepository;
import com.mendohard.api.repository.ComercioEstadoRepository;
import com.mendohard.api.repository.ConsumidorRepository;
import com.mendohard.api.repository.VendedorRepository;
import com.mendohard.api.security.JwtUtil;
import com.mendohard.api.service.IniciarSesionService;
import com.mendohard.api.service.strategy.ClaveStrategy;
import com.mendohard.api.service.factory.ClaveStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IniciarSesionServiceImpl implements IniciarSesionService {

    private final UsuarioRepository usuarioRepository;
    private final ClaveRepository claveRepository;
    private final IntentoFallidoRepository intentoFallidoRepository;
    private final VendedorEstadoRepository vendedorEstadoRepository;
    private final ComercioEstadoRepository comercioEstadoRepository;
    private final ConsumidorRepository consumidorRepository;
    private final VendedorRepository vendedorRepository;
    private final JwtUtil jwtUtil;
    private final ClaveStrategyFactory claveStrategyFactory;


    @Value("${mendohard.business.max-login-attempts}")
    private int maxLoginAttempts;


    @Override
    @Transactional(noRollbackFor = IniciarSesionException.class)
    public IniciarSesionResponseDTO procesarIngreso(IniciarSesionRequestDTO request) {
        log.info("Iniciando proceso de ingreso para email: {}", request.getEmail());

        // CA N°1: La validación de campos nulos/vacíos es delegada al framework
        // mediante las anotaciones @NotBlank y @Email del IniciarSesionRequestDTO.
        // Si el request llega aquí, los campos ya son válidos sintácticamente.

        // Buscar Usuario activo por email con validación de precondiciones
        Usuario usuario = usuarioRepository.findByEmailActivoYConPermisoIniciarSesion(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado, inactivo o no posee permiso 'iniciar_sesion' activo: {}", request.getEmail());
                    return new IniciarSesionException("Email o contraseña no validos");
                });

        // Lógica de Intentos Máximos
        LocalDate hoy = LocalDate.now();
        IntentoFallido intentoFallido = intentoFallidoRepository
                .findByUsuarioIdAndFecha(usuario.getId(), hoy)
                .orElseGet(() -> {
                    IntentoFallido nuevoIntento = IntentoFallido.builder()
                            .IFCodigo("IF-" + usuario.getId() + "-" + System.currentTimeMillis())
                            .IFCantidad(0)
                            .IFFecha(hoy)
                            .usuario(usuario)
                            .build();
                    return intentoFallidoRepository.save(nuevoIntento);
                });

        if (intentoFallido.getIFCantidad() >= maxLoginAttempts) {
            log.warn("Intentos máximos alcanzados para el usuario ID: {}", usuario.getId());
            throw new IntentosMaximosException("Intentos máximos de iniciar secion alcanzado");
        }

        //  Comprobar clave
        Clave clave = claveRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> {
                    log.error("Consistencia RDBM rota: El usuario ID {} no posee entidad Clave asociada", usuario.getId());
                    return new IniciarSesionException("Email o contraseña no validos");
                });

        //  APLICACIÓN DEL PATRÓN STRATEGY DELEGADO
        ClaveStrategy strategy = claveStrategyFactory.getStrategy(usuario.getAlgoritmoClave().getACNombre());
        boolean contraseñaValida = strategy.verificarContrasena(request.getContraseña(), clave.getCContrasena(), clave.getCSalt());

        // CA N°2: Si la contraseña NO coincide (Camino Alternativo N°2)
        if (!contraseñaValida) {
            log.warn("Contraseña no válida para el usuario ID: {}", usuario.getId());

            intentoFallido.setIFCantidad(intentoFallido.getIFCantidad() + 1);
            com.mendohard.api.model.IntentoFallido guardado = intentoFallidoRepository.save(intentoFallido);

            // Lanza IniciarSesionException con el campo afectado y la cantidad actualizada de intentos
            throw new IniciarSesionException(
                    "Contraseña no valida",
                    List.of("contraseña"),
                    guardado.getIFCantidad()
            );
        }

        // Si la contraseña COINCIDE - Enrutamiento final por rol
        String redireccionHome = determinarRedireccion(usuario.getRol().getRNombre());
        log.info("Autenticación exitosa. Generando Token de acceso para '{}'", usuario.getUEmail());

        // Extraer permisos activos del Rol del Usuario
        List<String> permisos = usuario.getRol().getRolPermisos().stream()
                .filter(rp -> rp.getRPFechaHasta() == null)
                .map(com.mendohard.api.model.RolPermiso::getPermiso)
                .filter(p -> p.getPFechaBaja() == null)
                .map(com.mendohard.api.model.Permiso::getPNombre)
                .toList();

        String tokenGenerado = jwtUtil.generarToken(usuario.getUEmail(), usuario.getRol().getRNombre(), permisos);

        IniciarSesionResponseDTO.IniciarSesionResponseDTOBuilder responseBuilder = IniciarSesionResponseDTO.builder()
                .email(usuario.getUEmail())
                .rolNombre(usuario.getRol().getRNombre())
                .nombreCompleto(usuario.getUNombre() + " " + usuario.getUApellido())
                .redireccionHome(redireccionHome)
                .token(tokenGenerado);

        if ("Responsable MendoHard".equals(usuario.getRol().getRNombre())) {
            Long usuariosTotales = consumidorRepository.countConsumidoresActivos() + vendedorRepository.countVendedoresAceptadosActivos();
            
            IniciarSesionResponseDTO.ExtraDataResponsableDTO extraData = IniciarSesionResponseDTO.ExtraDataResponsableDTO.builder()
                    .usuariosTotales(usuariosTotales)
                    .vendedoresPendientes(vendedorEstadoRepository.countVendedoresPendientes())
                    .comerciosActivos(comercioEstadoRepository.countComerciosActivos())
                    .build();
            responseBuilder.extraData(extraData);
        }

        return responseBuilder.build();
    }

    private String determinarRedireccion(String rolNombre) {
        return switch (rolNombre) {
            case "Consumidor" -> "UI_02 / Home Consumidor";
            case "Vendedor" -> "UI_05 / Home Vendedor";
            case "Responsable MendoHard" -> "UI_06 / Home Responsable MendoHard";
            default -> "UI_01 / Home Default";
        };
    }
}