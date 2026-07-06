package com.mendohard.api.service.impl;


import com.mendohard.api.dto.RegistrarResponsableMendoHardRequestDTO;
import com.mendohard.api.dto.RegistrarResponsableMendoHardResponseDTO;
import com.mendohard.api.exception.*;
import com.mendohard.api.model.AlgoritmoClave;
import com.mendohard.api.model.Clave;
import com.mendohard.api.model.ResponsableMendoHard;
import com.mendohard.api.model.Rol;
import com.mendohard.api.repository.AlgoritmoClaveRepository;
import com.mendohard.api.repository.ClaveRepository;
import com.mendohard.api.repository.RolRepository;
import com.mendohard.api.repository.UsuarioRepository;
import com.mendohard.api.security.JwtUtil;
import com.mendohard.api.service.ResponsableMendoHardService;
import com.mendohard.api.service.factory.ClaveStrategyFactory;
import com.mendohard.api.service.strategy.ClaveStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ResponsableMendoHardServiceImpl implements ResponsableMendoHardService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AlgoritmoClaveRepository algoritmoClaveRepository;
    private final ClaveRepository claveRepository;
    private final ClaveStrategyFactory claveStrategyFactory;
    private final JwtUtil jwtUtil;

    public ResponsableMendoHardServiceImpl(UsuarioRepository usuarioRepository,
                                          RolRepository rolRepository,
                                          AlgoritmoClaveRepository algoritmoClaveRepository,
                                          ClaveRepository claveRepository,
                                          ClaveStrategyFactory claveStrategyFactory,
                                          JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.algoritmoClaveRepository = algoritmoClaveRepository;
        this.claveRepository = claveRepository;
        this.claveStrategyFactory = claveStrategyFactory;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public RegistrarResponsableMendoHardResponseDTO registrarResponsable(
            RegistrarResponsableMendoHardRequestDTO request,
            String authHeader) {

        // Validar Precondición (Actor autenticado y con permiso)
        String token = authHeader.substring(7);
        String emailSesion = jwtUtil.extraerEmail(token);

        if (!usuarioRepository.hasPermisoRegistrarResponsable(emailSesion)) {
            throw new AccesoDenegadoException("No posee permisos para registrar un Responsable MendoHard.");
        }

        // CA N°2: Contraseña no igual a confirmación contraseña
        if (!request.contrasenna().equals(request.confirmacionContrasenna())) {
            throw new ContrasennaNoCoincideException("La contraseña no coincide con la confirmación.");
        }


        if (usuarioRepository.existsByUEmailActivo(request.uEmail()) ||
                usuarioRepository.existsByRMHLegajoActivo(request.rmhLegajo())) {
            throw new UsuarioYaExisteException("Ya existe este usuario");
        }

        // Buscar instancias activas de Rol y AlgoritmoClave
        Rol rol = rolRepository.findByRNombreActivo("Responsable MendoHard")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado en el sistema"));

        AlgoritmoClave algoritmo = algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema")
                .orElseThrow(() -> new RuntimeException("Algoritmo de clave no encontrado"));

        //  Crear Instancia de ResponsableMendoHard
        ResponsableMendoHard nuevoResponsable = ResponsableMendoHard.builder()
                .UCodigo("TEMP")
                .UNombre(request.uNombre())
                .UApellido(request.uApellido())
                .UEmail(request.uEmail())
                .RMHLegajo(request.rmhLegajo())
                .UFechaAlta(LocalDate.now())
                .UFechaBaja(null)
                .rol(rol)
                .algoritmoClave(algoritmo)
                .build();

        //  Persistir Usuario para generar ID y luego actualizar UCodigo definitivo
        nuevoResponsable = usuarioRepository.save(nuevoResponsable);
        nuevoResponsable.setUCodigo("RMH-" + nuevoResponsable.getId());
        usuarioRepository.save(nuevoResponsable);

        //  Obtener estrategia y generar clave
        ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmo.getACNombre());
        strategy.generarYGuardarClave(nuevoResponsable, request.contrasenna());

        //  Retorno exitoso
        return new RegistrarResponsableMendoHardResponseDTO(
                nuevoResponsable.getUCodigo(),
                "Administrador registrado con éxito"
        );
    }
}
