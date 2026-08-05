package com.mendohard.api.service.impl;

import com.mendohard.api.dto.RegistroConsumidorRequestDTO;
import com.mendohard.api.dto.RegistroResponseDTO;
import com.mendohard.api.dto.RegistroVendedorRequestDTO;
import com.mendohard.api.dto.UbicacionesVendedorDTO;
import com.mendohard.api.exception.ContrasennaNoCoincideException;
import com.mendohard.api.exception.RegistroException;
import com.mendohard.api.exception.UsuarioYaExisteException;
import com.mendohard.api.model.*;
import com.mendohard.api.repository.*;
import com.mendohard.api.service.RegistroService;
import com.mendohard.api.service.strategy.ClaveStrategy;
import com.mendohard.api.service.factory.ClaveStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistroServiceImpl implements RegistroService {

        private final ConsumidorRepository consumidorRepository;
        private final VendedorRepository vendedorRepository;
        private final ComercioRepository comercioRepository;
        private final DepartamentoRepository departamentoRepository;
        private final EstadoVendedorRepository estadoVendedorRepository;
        private final EstadoComercioRepository estadoComercioRepository;
        private final VendedorEstadoRepository vendedorEstadoRepository;
        private final ComercioEstadoRepository comercioEstadoRepository;
        private final AlgoritmoClaveRepository algoritmoClaveRepository;
        private final ClaveStrategyFactory claveStrategyFactory;

        private final RolRepository rolRepository;
        private final UsuarioRepository usuarioRepository;

        @Override
        public List<UbicacionesVendedorDTO> obtenerUbicacionesVendedor() {
                log.info("Obteniendo ubicaciones disponibles para registro de vendedor");

                List<Departamento> departamentosActivos = departamentoRepository.findAllActivos();

                List<UbicacionesVendedorDTO> ubicaciones = departamentosActivos.stream()
                                .map(departamento -> {
                                        Provincia provincia = departamento.getProvincia();
                                        Pais pais = provincia.getPais();

                                        return UbicacionesVendedorDTO.builder()
                                                        .DCodigo(departamento.getDCodigo())
                                                        .DNombre(departamento.getDNombre())
                                                        .ProCodigo(provincia.getProCodigo())
                                                        .ProNombre(provincia.getProNombre())
                                                        .PCodigo(pais.getPCodigo())
                                                        .PNombre(pais.getPNombre())
                                                        .build();
                                })
                                .collect(Collectors.toList());

                log.info("Se retornaron {} ubicaciones disponibles", ubicaciones.size());
                return ubicaciones;
        }

        @Override
        @Transactional
        public RegistroResponseDTO registrarConsumidor(RegistroConsumidorRequestDTO request) {
                log.info("Iniciando registro de consumidor con email: {}", request.getUEmail());

                validarContraseñasConsumidorCoincidan(request.getContrasena(), request.getConfirmacionContrasena());
                validarUnicidadConsumidor(request.getUEmail(), request.getCApodo());

                // Cargamos el algoritmo real de la base de datos para conocer su nombre maestro
                AlgoritmoClave algoritmoClave = algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema")
                                .orElseThrow(() -> new RegistroException(
                                                "Algoritmo de clave base no configurado en el sistema"));

                Rol rol = rolRepository.findByRNombreActivo("Consumidor")
                                .orElseThrow(() -> new RegistroException(
                                                "Rol 'Consumidor' no encontrado en el sistema"));

                Consumidor consumidor = Consumidor.builder()
                                .UNombre(request.getUNombre())
                                .UApellido(request.getUApellido())
                                .UEmail(request.getUEmail())
                                .CApodo(request.getCApodo())
                                .UCodigo("TEMP")
                                .UFechaAlta(LocalDate.now())
                                .algoritmoClave(algoritmoClave)
                                .rol(rol)
                                .build();

                consumidor = consumidorRepository.save(consumidor);
                Long consumidorId = consumidor.getId();
                log.info("Consumidor creado con ID: {}", consumidorId);

                String codigoConsumidor = "CONS-" + consumidorId;
                consumidor.setUCodigo(codigoConsumidor);
                consumidor = consumidorRepository.save(consumidor);
                log.info("Código de consumidor generado: {}", codigoConsumidor);

                // DELEGACIÓN AL PATRÓN FACTORY + STRATEGY
                ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmoClave.getACNombre());
                strategy.generarYGuardarClave(consumidor, request.getContrasena());

                return RegistroResponseDTO.builder()
                                .usuarioId(consumidorId)
                                .UCodigo(codigoConsumidor)
                                .UEmail(request.getUEmail())
                                .UNombre(request.getUNombre())
                                .tipoUsuario("Consumidor")
                                .build();
        }

        @Override
        @Transactional
        public RegistroResponseDTO registrarVendedor(RegistroVendedorRequestDTO request) {
                log.info("Iniciando registro de vendedor con email: {}", request.getUEmail());

                validarContraseñasVendedorCoincidan(request.getContrasena(), request.getConfirmacionContrasena());
                validarUnicidadVendedor(request.getUEmail(), request.getVCuit());

                Departamento departamento = departamentoRepository.findByCodigoDepartamento(request.getDCodigo())
                                .orElseThrow(() -> new RegistroException(
                                                "Departamento no encontrado con código: " + request.getDCodigo()));

                EstadoVendedor estadoVendedor = estadoVendedorRepository.findByNombreActivo("VendedorPendiente")
                                .orElseThrow(() -> new RegistroException("Estado 'VendedorPendiente' no encontrado"));

                EstadoComercio estadoComercio = estadoComercioRepository.findByNombreActivo("ComercioPendiente")
                                .orElseThrow(() -> new RegistroException("Estado 'ComercioPendiente' no encontrado"));

                // Cargamos el algoritmo real de la base de datos para conocer su nombre maestro
                AlgoritmoClave algoritmoClave = algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema")
                                .orElseThrow(() -> new RegistroException(
                                                "Algoritmo de clave base no configurado en el sistema"));

                Rol rol = rolRepository.findByRNombreActivo("Vendedor")
                                .orElseThrow(() -> new RegistroException("Rol 'Vendedor' no encontrado en el sistema"));

                Vendedor vendedor = Vendedor.builder()
                                .UNombre(request.getUNombre())
                                .UApellido(request.getUApellido())
                                .UEmail(request.getUEmail())
                                .VTelefono(request.getVTelefono())
                                .VCuit(request.getVCuit())
                                .VRazonSocial(request.getVRazonSocial())
                                .VCategoriaFiscal(request.getVCategoriaFiscal())
                                .UCodigo("TEMP")
                                .UFechaAlta(LocalDate.now())
                                .algoritmoClave(algoritmoClave)
                                .rol(rol)
                                .vendedorEstados(new ArrayList<>())
                                .comercios(new ArrayList<>())
                                .build();

                vendedor = vendedorRepository.save(vendedor);
                Long vendedorId = vendedor.getId();
                log.info("Vendedor creado con ID: {}", vendedorId);

                String codigoVendedor = "V-" + vendedorId;
                vendedor.setUCodigo(codigoVendedor);
                vendedor = vendedorRepository.save(vendedor);
                log.info("Código de vendedor generado: {}", codigoVendedor);

                // DELEGACIÓN AL PATRÓN FACTORY + STRATEGY
                ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmoClave.getACNombre());
                strategy.generarYGuardarClave(vendedor, request.getContrasena());

                VendedorEstado vendedorEstado = VendedorEstado.builder()
                                .VEFechaDesde(LocalDate.now())
                                .VEFechaHasta(null)
                                .estadoVendedor(estadoVendedor)
                                .build();

                vendedorEstadoRepository.save(vendedorEstado);
                vendedor.getVendedorEstados().add(vendedorEstado);
                log.info("VendedorEstado 'VendedorPendiente' asociado con fecha desde: {}", LocalDate.now());

                Comercio comercio = Comercio.builder()
                                .CNombreFantasia(request.getCNombreFantasia())
                                .CTelefono(request.getCTelefono())
                                .CDireccionCalle(request.getCDireccionCalle())
                                .CNumeroEnCalle(request.getCNumeroEnCalle())
                                .CLatitud(request.getCLatitud())
                                .CLongitud(request.getCLongitud())
                                .CHorarioAtencion(request.getCHorarioAtencion())
                                .CFechaSolicitud(LocalDate.now())
                                .CFechaAlta(null)
                                .CFechaBaja(null)
                                .CCodigo("TEMP")
                                .departamento(departamento)
                                .comercioEstados(new ArrayList<>())
                                .build();

                comercio = comercioRepository.save(comercio);
                Long comercioId = comercio.getId();
                log.info("Comercio creado con ID: {}", comercioId);

                String codigoComercio = "C-" + comercioId;
                comercio.setCCodigo(codigoComercio);
                comercio = comercioRepository.save(comercio);
                log.info("Código de comercio generado: {}", codigoComercio);

                ComercioEstado comercioEstado = ComercioEstado.builder()
                                .CEFechaDesde(LocalDate.now())
                                .CEFechaHasta(null)
                                .estadoComercio(estadoComercio)
                                .build();

                comercioEstadoRepository.save(comercioEstado);
                comercio.getComercioEstados().add(comercioEstado);
                comercio = comercioRepository.save(comercio);
                log.info("ComercioEstado 'ComercioPendiente' asociado con fecha desde: {}", LocalDate.now());

                vendedor.getComercios().add(comercio);
                vendedorRepository.save(vendedor);
                log.info("Comercio asociado al vendedor con ID: {}", vendedorId);

                log.info("Registro de vendedor completado con ID: {}", vendedorId);

                return RegistroResponseDTO.builder()
                                .usuarioId(vendedorId)
                                .UCodigo(codigoVendedor)
                                .UEmail(request.getUEmail())
                                .UNombre(request.getUNombre())
                                .tipoUsuario("Vendedor")
                                .build();
        }

        // ─────────────────────────────────────────────────────────────────────────
        // Métodos de validación de negocio — CU-02
        // ─────────────────────────────────────────────────────────────────────────

        /**
         * CA N°2 de CU-02 (Consumidor): contraseña no coincide con confirmación.
         * Lanza ContrasennaNoCoincideException indicando los campos exactos del DTO.
         */
        private void validarContraseñasConsumidorCoincidan(String contrasena, String confirmacion) {
                if (!contrasena.equals(confirmacion)) {
                        throw new ContrasennaNoCoincideException(
                                        "La contraseña no coincide con la confirmación de contraseña",
                                        List.of("Contrasena", "ConfirmacionContrasena"));
                }
        }

        /**
         * CA N°2 de CU-02 (Vendedor): contraseña no coincide con confirmación.
         * Lanza ContrasennaNoCoincideException indicando los campos exactos del DTO.
         */
        private void validarContraseñasVendedorCoincidan(String contrasena, String confirmacion) {
                if (!contrasena.equals(confirmacion)) {
                        throw new ContrasennaNoCoincideException(
                                        "La contraseña ingresada es distinta a la confirmación de la contraseña",
                                        List.of("Contrasena", "ConfirmacionContrasena"));
                }
        }

        /**
         * CA N°3/8 de CU-02 (Consumidor): duplicidad de Email o Apodo.
         * Lanza UsuarioYaExisteException indicando el campo duplicado exacto.
         */
        private void validarUnicidadConsumidor(String email, String apodo) {
                if (usuarioRepository.existsByUEmailActivo(email)) {
                        throw new UsuarioYaExisteException(
                                        "Ya existe un usuario registrado con el mismo Email o Apodo",
                                        List.of("UEmail"));
                }
                if (consumidorRepository.findByApodoActivo(apodo).isPresent()) {
                        throw new UsuarioYaExisteException(
                                        "Ya existe un Consumidor registrado con el mismo Email o Apodo, cambiarlo",
                                        List.of("CApodo"));
                }
        }

        /**
         * CA N°3 de CU-02 (Vendedor): duplicidad de Email o CUIT.
         * Lanza UsuarioYaExisteException indicando el campo duplicado exacto.
         */
        private void validarUnicidadVendedor(String email, String cuit) {
                if (usuarioRepository.existsByUEmailActivo(email)) {
                        throw new UsuarioYaExisteException(
                                        "Ya existe un usuario registrado con los datos ingresados",
                                        List.of("UEmail"));
                }
                if (vendedorRepository.findByCuitActivo(cuit).isPresent()) {
                        throw new UsuarioYaExisteException(
                                        "Ya existe un comercio registrado con los datos ingresados",
                                        List.of("VCuit"));
                }
        }
}