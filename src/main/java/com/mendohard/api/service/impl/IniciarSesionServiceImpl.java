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
import com.mendohard.api.security.JwtUtil;
import com.mendohard.api.service.IniciarSesionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class IniciarSesionServiceImpl implements IniciarSesionService {

    private final UsuarioRepository usuarioRepository;
    private final ClaveRepository claveRepository;
    private final IntentoFallidoRepository intentoFallidoRepository;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(noRollbackFor = IniciarSesionException.class)
    public IniciarSesionResponseDTO procesarIngreso(IniciarSesionRequestDTO request) {
        log.info("Iniciando proceso de ingreso para email: {}", request.getEmail());

        // a) Controlar consistencia de datos ingresados
        if (request.getEmail() == null || request.getEmail().isBlank() ||
                request.getContraseña() == null || request.getContraseña().isBlank()) {
            log.warn("Email o contraseña vacíos en los parámetros");
            throw new IniciarSesionException("Email o contraseña no validos");
        }

        // b) Buscar Usuario activo por email con validación de precondiciones:
        //    - Usuario debe estar activo (UFechaBaja IS NULL)
        //    - Su Rol debe estar activo (RFechaBaja IS NULL)
        //    - Debe poseer el Permiso 'iniciar_sesion' activo en su Rol
        //      (RolPermiso.FechaHasta IS NULL y Permiso.FechaBaja IS NULL)
        Usuario usuario = usuarioRepository.findByEmailActivoYConPermisoIniciarSesion(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado, inactivo o no posee permiso 'iniciar_sesion' activo: {}", request.getEmail());
                    return new IniciarSesionException("Email o contraseña no validos");
                });

        // c) Lógica de Intentos Máximos
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

        // Comprobar que IFCantidad < 10
        if (intentoFallido.getIFCantidad() >= 10) {
            log.warn("Intentos máximos alcanzados para el usuario ID: {}", usuario.getId());
            throw new IntentosMaximosException("Intentos máximos de iniciar secion alcanzado");
        }

        // d) Comprobar clave
        Clave clave = claveRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> {
                    log.error("Consistencia RDBM rota: El usuario ID {} no posee entidad Clave asociada", usuario.getId());
                    return new IniciarSesionException("Email o contraseña no validos");
                });

        String textoAHasher = request.getContraseña() + clave.getCSalt();
        // Convertimos la concatenación en un hash MD5 real en formato Hexadecimal
        String contraseñaIngresadaHash = DigestUtils.md5DigestAsHex(textoAHasher.getBytes());

        boolean contraseñaValida = clave.getCContrasena().equals(contraseñaIngresadaHash);

        // e) Si la contraseña NO coincide (Camino Alternativo N°2)
        if (!contraseñaValida) {
            log.warn("Contraseña no válida para el usuario ID: {}", usuario.getId());

            // Incrementamos y persistimos el atributo IFCantidad
            intentoFallido.setIFCantidad(intentoFallido.getIFCantidad() + 1);
            com.mendohard.api.model.IntentoFallido guardado = intentoFallidoRepository.save(intentoFallido);

            // Pasamos el mensaje de la especificación y el valor de su atributo 'cantidad'
            throw new IniciarSesionException("Contraseña no valida", guardado.getIFCantidad());
        }

        // f) Si la contraseña COINCIDE - Enrutamiento final por rol
        String redireccionHome = determinarRedireccion(usuario.getRol().getRNombre());
        log.info("Autenticación exitosa. Generando Token de acceso para '{}'", usuario.getUEmail());

        // Generamos el token de forma segura
        String tokenGenerado = jwtUtil.generarToken(usuario.getUEmail(), usuario.getRol().getRNombre());

        return IniciarSesionResponseDTO.builder()
                .email(usuario.getUEmail())
                .rolNombre(usuario.getRol().getRNombre())
                .nombreCompleto(usuario.getUNombre() + " " + usuario.getUApellido())
                .redireccionHome(redireccionHome)
                .token(tokenGenerado)
                .build();
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