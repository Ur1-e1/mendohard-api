package com.mendohard.api;

import com.mendohard.api.model.*;
import com.mendohard.api.repository.*;
import com.mendohard.api.service.factory.ClaveStrategyFactory;
import com.mendohard.api.service.strategy.ClaveStrategy;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DataSeeder: Persiste los datos maestros necesarios para que la API funcione correctamente.
 * Se ejecuta automáticamente al arrancar la aplicación.
 * Es idempotente: verifica la existencia por conteo antes de insertar para evitar duplicados.
 * No modifica ningún repositorio existente — usa únicamente los métodos que ya tenía cada uno.
 */

import org.springframework.context.annotation.Profile;

@Profile("dev")
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

        private final AlgoritmoClaveRepository algoritmoClaveRepository;
        private final PermisoRepository permisoRepository;
        private final RolRepository rolRepository;
        private final EstadoVendedorRepository estadoVendedorRepository;
        private final EstadoComercioRepository estadoComercioRepository;
        private final PaisRepository paisRepository;
        private final ProvinciaRepository provinciaRepository;
        private final DepartamentoRepository departamentoRepository;
        private final UsuarioRepository usuarioRepository;
        private final ClaveStrategyFactory claveStrategyFactory;
        private final CategoriaRepository categoriaRepository;
        private final ProductoRepository productoRepository;
        private final EstadoConsultaStockRepository estadoConsultaStockRepository;
        private final NivelStockRepository nivelStockRepository;
        private final ConsultaStockRepository consultaStockRepository;
        private final ComercioRepository comercioRepository;

        @Override
        @Transactional
        public void run(String... args) {
                log.info("=== Iniciando DataSeeder ===");

                seedAlgoritmosClave();
                seedPermisos();
                seedRoles();
                seedEstadosVendedor();
                seedEstadosComercio();
                seedUbicaciones();
                seedAdminRMH();
                seedConsumidor();
                seedVendedorYComercio();
                seedCategoriasYProductos();
                seedEstadosConsultaStock();
                seedNivelesStock();
                seedConsultasStock();

                log.info("=== DataSeeder completado ===");
        }

        // ─── RESPONSABLE MENDOHARD INICIAL ───────────────────────────────────────────

        private void seedAdminRMH() {
                // existsByUEmailActivo ya existe en UsuarioRepository
                if (usuarioRepository.existsByUEmailActivo("admin@mendohard.com")) {
                        log.info("[DataSeeder] Admin RMH ya existe, se omite.");
                        return;
                }

                AlgoritmoClave algoritmo = algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema")
                                .orElseThrow(() -> new IllegalStateException(
                                                "AlgoritmoClave no encontrado al crear admin RMH"));

                Rol rolRMH = rolRepository.findByRNombreActivo("Responsable MendoHard")
                                .orElseThrow(() -> new IllegalStateException(
                                                "Rol 'Responsable MendoHard' no encontrado al crear admin RMH"));

                ResponsableMendoHard admin = ResponsableMendoHard.builder()
                                .UCodigo("TEMP")
                                .UNombre("Admin")
                                .UApellido("Sistema")
                                .UEmail("admin@mendohard.com")
                                .RMHLegajo("LEG-001")
                                .UFechaAlta(LocalDate.now())
                                .UFechaBaja(null)
                                .rol(rolRMH)
                                .algoritmoClave(algoritmo)
                                .build();

                admin = (ResponsableMendoHard) usuarioRepository.save(admin);
                admin.setUCodigo("RMH-" + admin.getId());
                usuarioRepository.save(admin);

                ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmo.getACNombre());
                strategy.generarYGuardarClave(admin, "admin1234");

                log.info("[DataSeeder] Admin RMH creado → email: admin@mendohard.com / contraseña: admin1234");
        }

        // ─── ALGORITMOS DE CLAVE ─────────────────────────────────────────────────────

        private void seedAlgoritmosClave() {
                // findByACNombreActivo ya existe en AlgoritmoClaveRepository
                if (algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema").isPresent()) {
                        log.info("[DataSeeder] AlgoritmoClave 'ContraseñaEnSistema' ya existe, se omite.");
                        return;
                }
                AlgoritmoClave algoritmo = AlgoritmoClave.builder()
                                .ACCodigo("AC-001")
                                .ACNombre("ContraseñaEnSistema")
                                .ACFechaBaja(null)
                                .build();
                algoritmoClaveRepository.save(algoritmo);
                log.info("[DataSeeder] AlgoritmoClave 'ContraseñaEnSistema' creado.");
        }

        // ─── PERMISOS ────────────────────────────────────────────────────────────────

        private void seedPermisos() {
                if (permisoRepository.count() > 0) {
                        // Agregar el nuevo permiso de stock de forma idempotente
                        if (permisoRepository.findByPCodigoAndPFechaBajaIsNull("PERM-013").isEmpty()) {
                                permisoRepository.save(buildPermiso("PERM-013", "consultar_stock",
                                                "Permite consultar el stock de productos en los comercios"));
                                log.info("[DataSeeder] Permiso consultar_stock (PERM-013) añadido independientemente.");
                        }
                        if (permisoRepository.findByPCodigoAndPFechaBajaIsNull("PERM-014").isEmpty()) {
                                permisoRepository.save(buildPermiso("PERM-014", "confirmar_stock",
                                                "Permite al vendedor confirmar el stock de los productos"));
                                log.info("[DataSeeder] Permiso confirmar_stock (PERM-014) añadido independientemente.");
                        }
                        if (permisoRepository.findByPCodigoAndPFechaBajaIsNull("PERM-015").isEmpty()) {
                                permisoRepository.save(buildPermiso("PERM-015", "ver_metricas",
                                                "Permite visualizar las métricas de demanda"));
                                log.info("[DataSeeder] Permiso ver_metricas (PERM-015) añadido independientemente.");
                        }
                        if (permisoRepository.findByPCodigoAndPFechaBajaIsNull("PERM-016").isEmpty()) {
                                permisoRepository.save(buildPermiso("PERM-016", "ver_metricas_responsable",
                                                "Permite visualizar las métricas del dashboard del Responsable MendoHard"));
                                log.info("[DataSeeder] Permiso ver_metricas_responsable (PERM-016) añadido independientemente.");
                        }
                        log.info("[DataSeeder] Permisos base ya existen, se omiten.");
                        return;
                }
                List<Permiso> permisos = List.of(
                                buildPermiso("PERM-001", "iniciar_sesion",
                                                "Permite al usuario iniciar sesión en el sistema"),
                                buildPermiso("PERM-002", "registrar_responsablemendohard",
                                                "Permite registrar un nuevo Responsable MendoHard"),
                                buildPermiso("PERM-003", "gestionar_comercios",
                                                "Permite aprobar/rechazar comercios pendientes"),
                                buildPermiso("PERM-004", "ver_productos",
                                                "Permite visualizar el catálogo de productos"),
                                buildPermiso("PERM-005", "gestionar_productos",
                                                "Permite cargar y editar productos propios"),
                                buildPermiso("PERM-006", "modificar_perfil",
                                                "Permite al usuario consultar y modificar su información de perfil"),
                                buildPermiso("PERM-007", "recuperar_credencial",
                                                "Permite recuperar la contraseña de acceso mediante un código OTP"),
                                buildPermiso("PERM-008", "validar_vendedor",
                                                "Permite validar (aceptar o rechazar) a los vendedores pendientes"),
                                buildPermiso("PERM-009", "validar_comercio",
                                                "Permite validar (aceptar o rechazar) a los comercios pendientes"),
                                buildPermiso("PERM-010", "registrar_comercio",
                                                "Permite registrar un nuevo comercio en la plataforma"),
                                buildPermiso("PERM-011", "buscar_producto",
                                                "Permite buscar productos por categoría"),
                                buildPermiso("PERM-012", "abm_producto",
                                                "Permite realizar el alta, baja y modificación de productos de hardware"),
                                buildPermiso("PERM-013", "consultar_stock",
                                                "Permite consultar el stock de productos en los comercios"),
                                buildPermiso("PERM-014", "confirmar_stock",
                                                "Permite al vendedor confirmar el stock de los productos"),
                                buildPermiso("PERM-015", "ver_metricas",
                                                "Permite visualizar las métricas de demanda"),
                                buildPermiso("PERM-016", "ver_metricas_responsable",
                                                "Permite visualizar las métricas del dashboard del Responsable MendoHard"));
                permisoRepository.saveAll(permisos);
                log.info("[DataSeeder] {} permisos creados.", permisos.size());
        }

        private Permiso buildPermiso(String codigo, String nombre, String descripcion) {
                return Permiso.builder()
                                .PCodigo(codigo)
                                .PNombre(nombre)
                                .PDescripcion(descripcion)
                                .PFechaAlta(LocalDate.now())
                                .PFechaBaja(null)
                                .build();
        }

        // ─── ROLES ───────────────────────────────────────────────────────────────────

        private void seedRoles() {
                // findByRNombreActivo ya existe en RolRepository
                if (rolRepository.count() > 0) {
                        log.info("[DataSeeder] Roles ya existen ({} registros), verificando actualización.",
                                        rolRepository.count());
                        Optional<Rol> rolOpt = rolRepository.findByRNombreActivo("Consumidor");
                        if (rolOpt.isPresent()) {
                                Rol consumidor = rolOpt.get();
                                boolean tienePermiso = consumidor.getRolPermisos().stream()
                                                .anyMatch(rp -> "consultar_stock".equals(rp.getPermiso().getPNombre()));
                                if (!tienePermiso) {
                                        Permiso pConsultarStock = permisoRepository
                                                        .findByPCodigoAndPFechaBajaIsNull("PERM-013").orElseThrow();
                                        RolPermiso nuevoRolPermiso = buildRolPermiso(pConsultarStock);
                                        consumidor.getRolPermisos().add(nuevoRolPermiso);
                                        rolRepository.save(consumidor);
                                        log.info("[DataSeeder] Permiso consultar_stock añadido al rol Consumidor de forma idempotente.");
                                }
                        }
                        Optional<Rol> rolOptVendedor = rolRepository.findByRNombreActivo("Vendedor");
                        if (rolOptVendedor.isPresent()) {
                                Rol vendedor = rolOptVendedor.get();
                                boolean tienePermiso = vendedor.getRolPermisos().stream()
                                                .anyMatch(rp -> "confirmar_stock".equals(rp.getPermiso().getPNombre()));
                                if (!tienePermiso) {
                                        Permiso pConfirmarStock = permisoRepository
                                                        .findByPCodigoAndPFechaBajaIsNull("PERM-014").orElseThrow();
                                        RolPermiso nuevoRolPermiso = buildRolPermiso(pConfirmarStock);
                                        vendedor.getRolPermisos().add(nuevoRolPermiso);
                                        rolRepository.save(vendedor);
                                        log.info("[DataSeeder] Permiso confirmar_stock añadido y persistido explícitamente al rol Vendedor.");
                                }
                                boolean tienePermisoMetricas = vendedor.getRolPermisos().stream()
                                                .anyMatch(rp -> "ver_metricas".equals(rp.getPermiso().getPNombre()));
                                if (!tienePermisoMetricas) {
                                        Permiso pVerMetricas = permisoRepository
                                                        .findByPCodigoAndPFechaBajaIsNull("PERM-015").orElseThrow();
                                        RolPermiso nuevoRolPermiso = buildRolPermiso(pVerMetricas);
                                        vendedor.getRolPermisos().add(nuevoRolPermiso);
                                        rolRepository.save(vendedor);
                                        log.info("[DataSeeder] Permiso ver_metricas añadido y persistido explícitamente al rol Vendedor.");
                                }
                        }
                        Optional<Rol> rolOptRMH = rolRepository.findByRNombreActivo("Responsable MendoHard");
                        if (rolOptRMH.isPresent()) {
                                Rol rmh = rolOptRMH.get();
                                boolean tienePermisoMetricasRMH = rmh.getRolPermisos().stream()
                                                .anyMatch(rp -> "ver_metricas_responsable"
                                                                .equals(rp.getPermiso().getPNombre()));
                                if (!tienePermisoMetricasRMH) {
                                        Permiso pVerMetricasRMH = permisoRepository
                                                        .findByPCodigoAndPFechaBajaIsNull("PERM-016").orElseThrow();
                                        RolPermiso nuevoRolPermiso = buildRolPermiso(pVerMetricasRMH);
                                        rmh.getRolPermisos().add(nuevoRolPermiso);
                                        rolRepository.save(rmh);
                                        log.info("[DataSeeder] Permiso ver_metricas_responsable añadido y persistido explícitamente al rol Responsable MendoHard.");
                                }
                        }
                        return;
                }

                // Cargar permisos ya persistidos por findAll (PermisoRepository hereda de
                // JpaRepository)
                List<Permiso> todos = permisoRepository.findAll();
                Permiso pIniciarSesion = findPermiso(todos, "iniciar_sesion");
                Permiso pRegistrarRmh = findPermiso(todos, "registrar_responsablemendohard");
                Permiso pGestionarComercios = findPermiso(todos, "gestionar_comercios");
                Permiso pVerProductos = findPermiso(todos, "ver_productos");
                Permiso pGestionarProductos = findPermiso(todos, "gestionar_productos");
                Permiso pModificarPerfil = findPermiso(todos, "modificar_perfil");
                Permiso pRecuperarCredencial = findPermiso(todos, "recuperar_credencial");
                Permiso pValidarVendedor = findPermiso(todos, "validar_vendedor");
                Permiso pValidarComercio = findPermiso(todos, "validar_comercio");
                Permiso pRegistrarComercio = findPermiso(todos, "registrar_comercio");
                Permiso pBuscarProducto = findPermiso(todos, "buscar_producto");
                Permiso pAbmProducto = findPermiso(todos, "abm_producto");
                Permiso pConsultarStock = findPermiso(todos, "consultar_stock");
                Permiso pConfirmarStock = findPermiso(todos, "confirmar_stock");
                Permiso pVerMetricas = findPermiso(todos, "ver_metricas");
                Permiso pVerMetricasRMH = findPermiso(todos, "ver_metricas_responsable");

                // Rol Consumidor
                Rol consumidor = Rol.builder()
                                .RCodigo("ROL-001")
                                .RNombre("Consumidor")
                                .RDescripcion("Usuario consumidor de la plataforma MendoHard")
                                .RFechaAlta(LocalDate.now())
                                .RFechaBaja(null)
                                .rolPermisos(List.of(
                                                buildRolPermiso(pIniciarSesion),
                                                buildRolPermiso(pVerProductos),
                                                buildRolPermiso(pModificarPerfil),
                                                buildRolPermiso(pRecuperarCredencial),
                                                buildRolPermiso(pBuscarProducto),
                                                buildRolPermiso(pConsultarStock)))
                                .build();

                // Rol Vendedor
                Rol vendedor = Rol.builder()
                                .RCodigo("ROL-002")
                                .RNombre("Vendedor")
                                .RDescripcion("Usuario vendedor con comercio en MendoHard")
                                .RFechaAlta(LocalDate.now())
                                .RFechaBaja(null)
                                .rolPermisos(List.of(
                                                buildRolPermiso(pIniciarSesion),
                                                buildRolPermiso(pVerProductos),
                                                buildRolPermiso(pGestionarProductos),
                                                buildRolPermiso(pModificarPerfil),
                                                buildRolPermiso(pRecuperarCredencial),
                                                buildRolPermiso(pRegistrarComercio),
                                                buildRolPermiso(pConfirmarStock),
                                                buildRolPermiso(pVerMetricas)))
                                .build();

                // Rol Responsable MendoHard
                Rol rmh = Rol.builder()
                                .RCodigo("ROL-003")
                                .RNombre("Responsable MendoHard")
                                .RDescripcion("Administrador interno de la plataforma MendoHard")
                                .RFechaAlta(LocalDate.now())
                                .RFechaBaja(null)
                                .rolPermisos(List.of(
                                                buildRolPermiso(pIniciarSesion),
                                                buildRolPermiso(pRegistrarRmh),
                                                buildRolPermiso(pGestionarComercios),
                                                buildRolPermiso(pValidarVendedor),
                                                buildRolPermiso(pValidarComercio),
                                                buildRolPermiso(pAbmProducto),
                                                buildRolPermiso(pVerMetricasRMH)))
                                .build();

                rolRepository.saveAll(List.of(consumidor, vendedor, rmh));
                log.info("[DataSeeder] 3 roles creados: Consumidor, Vendedor, Responsable MendoHard.");
        }

        private RolPermiso buildRolPermiso(Permiso permiso) {
                return RolPermiso.builder()
                                .RPFechaDesde(LocalDate.now())
                                .RPFechaHasta(null)
                                .permiso(permiso)
                                .build();
        }

        private Permiso findPermiso(List<Permiso> todos, String nombre) {
                return todos.stream()
                                .filter(p -> nombre.equals(p.getPNombre()))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException(
                                                "Permiso '" + nombre + "' no encontrado durante el seeding de roles"));
        }

        private void seedEstadosConsultaStock() {
                if (estadoConsultaStockRepository.count() > 0) {
                        log.info("[DataSeeder] EstadosConsultaStock ya existen, se omiten.");
                        return;
                }
                List<EstadoConsultaStock> estados = List.of(
                                EstadoConsultaStock.builder().ECSCodigo("EC-001").ECSNombre("StockPendiente").build(),
                                EstadoConsultaStock.builder().ECSCodigo("EC-002").ECSNombre("StockDisponible").build(),
                                EstadoConsultaStock.builder().ECSCodigo("EC-003").ECSNombre("SinStock").build(),
                                EstadoConsultaStock.builder().ECSCodigo("EC-004").ECSNombre("ConsultaExpirada")
                                                .build());
                estadoConsultaStockRepository.saveAll(estados);
                log.info("[DataSeeder] {} EstadosConsultaStock creados.", estados.size());
        }

        // ─── ESTADOS ─────────────────────────────────────────────────────────────────

        private void seedEstadosVendedor() {
                // findByNombreActivo ya existe en EstadoVendedorRepository
                if (estadoVendedorRepository.findByNombreActivo("VendedorPendiente").isPresent()) {
                        log.info("[DataSeeder] EstadosVendedor ya existen, se omiten.");
                        return;
                }
                List<EstadoVendedor> estados = List.of(
                                buildEstadoVendedor("EV-001", "VendedorPendiente"),
                                buildEstadoVendedor("EV-002", "VendedorAceptado"),
                                buildEstadoVendedor("EV-003", "VendedorRechazado"),
                                buildEstadoVendedor("EV-004", "VendedorInhabilitado"));
                estadoVendedorRepository.saveAll(estados);
                log.info("[DataSeeder] {} EstadosVendedor creados.", estados.size());
        }

        private EstadoVendedor buildEstadoVendedor(String codigo, String nombre) {
                return EstadoVendedor.builder()
                                .EVCodigo(codigo)
                                .EVNombre(nombre)
                                .EVFechaBaja(null)
                                .build();
        }

        private void seedEstadosComercio() {
                // findByNombreActivo ya existe en EstadoComercioRepository
                if (estadoComercioRepository.findByNombreActivo("ComercioPendiente").isPresent()) {
                        log.info("[DataSeeder] EstadosComercio ya existen, se omiten.");
                        return;
                }
                List<EstadoComercio> estados = List.of(
                                buildEstadoComercio("EC-001", "ComercioPendiente"),
                                buildEstadoComercio("EC-002", "ComercioAceptado"),
                                buildEstadoComercio("EC-003", "ComercioRechazado"),
                                buildEstadoComercio("EC-004", "ComercioInhabilitado"));
                estadoComercioRepository.saveAll(estados);
                log.info("[DataSeeder] {} EstadosComercio creados.", estados.size());
        }

        private EstadoComercio buildEstadoComercio(String codigo, String nombre) {
                return EstadoComercio.builder()
                                .ECCodigo(codigo)
                                .ECNombre(nombre)
                                .ECFechaBaja(null)
                                .build();
        }

        // ─── UBICACIONES GEOGRÁFICAS ─────────────────────────────────────────────────

        private void seedUbicaciones() {
                // findAllActivos / findAllActivas ya existen en PaisRepository y
                // ProvinciaRepository
                if (!paisRepository.findAllActivos().isEmpty()) {
                        log.info("[DataSeeder] Ubicaciones ya existen, se omiten.");
                        return;
                }

                // País: Argentina
                Pais argentina = Pais.builder()
                                .PCodigo("ARG")
                                .PNombre("Argentina")
                                .PFechaAlta(LocalDate.now())
                                .PFechaBaja(null)
                                .build();
                argentina = paisRepository.save(argentina);
                log.info("[DataSeeder] País 'Argentina' creado.");

                // Provincia: Mendoza
                Provincia mendoza = Provincia.builder()
                                .ProCodigo("MZA")
                                .ProNombre("Mendoza")
                                .ProFechaAlta(LocalDate.now())
                                .ProFechaBaja(null)
                                .pais(argentina)
                                .build();
                mendoza = provinciaRepository.save(mendoza);
                log.info("[DataSeeder] Provincia 'Mendoza' creada.");

                // Departamentos de Mendoza
                List<Departamento> departamentos = List.of(
                                buildDepartamento("DEP-001", "Capital", mendoza),
                                buildDepartamento("DEP-002", "Godoy Cruz", mendoza),
                                buildDepartamento("DEP-003", "Guaymallén", mendoza),
                                buildDepartamento("DEP-004", "Maipú", mendoza),
                                buildDepartamento("DEP-005", "Las Heras", mendoza),
                                buildDepartamento("DEP-006", "Luján de Cuyo", mendoza));
                departamentoRepository.saveAll(departamentos);
                log.info("[DataSeeder] {} departamentos de Mendoza creados.", departamentos.size());
        }

        private Departamento buildDepartamento(String codigo, String nombre, Provincia provincia) {
                return Departamento.builder()
                                .DCodigo(codigo)
                                .DNombre(nombre)
                                .DFechaAlta(LocalDate.now())
                                .DFechaBaja(null)
                                .provincia(provincia)
                                .build();
        }

        // ─── USUARIOS DE PRUEBA (CONSUMIDOR Y VENDEDOR) ──────────────────────────────

        private void seedConsumidor() {
                if (usuarioRepository.existsByUEmailActivo("consumidor@mendohard.com")) {
                        log.info("[DataSeeder] Consumidor ya existe, se omite.");
                        return;
                }

                AlgoritmoClave algoritmo = algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema")
                                .orElseThrow(() -> new IllegalStateException("AlgoritmoClave no encontrado"));
                Rol rolConsumidor = rolRepository.findByRNombreActivo("Consumidor")
                                .orElseThrow(() -> new IllegalStateException("Rol Consumidor no encontrado"));

                Consumidor consumidor = Consumidor.builder()
                                .UCodigo("TEMP-C")
                                .UNombre("Juan")
                                .UApellido("Pérez")
                                .UEmail("consumidor@mendohard.com")
                                .CApodo("juancho_p")
                                .UFechaAlta(LocalDate.now())
                                .UFechaBaja(null)
                                .rol(rolConsumidor)
                                .algoritmoClave(algoritmo)
                                .build();

                consumidor = (Consumidor) usuarioRepository.save(consumidor);
                consumidor.setUCodigo("CON-" + consumidor.getId());
                usuarioRepository.save(consumidor);

                ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmo.getACNombre());
                strategy.generarYGuardarClave(consumidor, "consumidor1234");
                log.info("[DataSeeder] Consumidor de prueba creado.");
        }

        private void seedVendedorYComercio() {
                if (usuarioRepository.existsByUEmailActivo("vendedor@mendohard.com")) {
                        log.info("[DataSeeder] Vendedor ya existe, se omite.");
                        return;
                }

                AlgoritmoClave algoritmo = algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema")
                                .orElseThrow(() -> new IllegalStateException("AlgoritmoClave no encontrado"));
                Rol rolVendedor = rolRepository.findByRNombreActivo("Vendedor")
                                .orElseThrow(() -> new IllegalStateException("Rol Vendedor no encontrado"));

                Vendedor vendedor = Vendedor.builder()
                                .UCodigo("TEMP-V")
                                .UNombre("María")
                                .UApellido("Gómez")
                                .UEmail("vendedor@mendohard.com")
                                .VTelefono("2615551234")
                                .VCuit("27301234567")
                                .VRazonSocial("Mendoza Tech SRL")
                                .VCategoriaFiscal("Responsable Inscripto")
                                .UFechaAlta(LocalDate.now())
                                .UFechaBaja(null)
                                .rol(rolVendedor)
                                .algoritmoClave(algoritmo)
                                .build();

                EstadoVendedor estadoAceptado = estadoVendedorRepository.findByNombreActivo("VendedorAceptado")
                                .orElseThrow(() -> new IllegalStateException("Estado VendedorAceptado no encontrado"));

                VendedorEstado vendedorEstado = VendedorEstado.builder()
                                .VEFechaDesde(LocalDate.now())
                                .VEFechaHasta(null)
                                .estadoVendedor(estadoAceptado)
                                .build();
                vendedor.setVendedorEstados(new java.util.ArrayList<>(List.of(vendedorEstado)));

                List<Departamento> todosDeptos = departamentoRepository.findAll();
                Departamento deptoCapital = todosDeptos.stream().filter(d -> d.getDNombre().equals("Capital"))
                                .findFirst().orElse(todosDeptos.get(0));
                Departamento deptoGodoyCruz = todosDeptos.stream().filter(d -> d.getDNombre().equals("Godoy Cruz"))
                                .findFirst().orElse(todosDeptos.get(0));
                Departamento deptoGuaymallen = todosDeptos.stream().filter(d -> d.getDNombre().equals("Guaymallén"))
                                .findFirst().orElse(todosDeptos.get(0));
                Departamento deptoMaipu = todosDeptos.stream().filter(d -> d.getDNombre().equals("Maipú")).findFirst()
                                .orElse(todosDeptos.get(0));
                Departamento deptoLasHeras = todosDeptos.stream().filter(d -> d.getDNombre().equals("Las Heras"))
                                .findFirst().orElse(todosDeptos.get(0));
                Departamento deptoLujan = todosDeptos.stream().filter(d -> d.getDNombre().equals("Luján de Cuyo"))
                                .findFirst().orElse(todosDeptos.get(0));

                EstadoComercio estadoComAceptado = estadoComercioRepository.findByNombreActivo("ComercioAceptado")
                                .orElseThrow(() -> new IllegalStateException("Estado ComercioAceptado no encontrado"));

                Comercio c1 = buildComercio("COM-001", "HardMza Central", "2614441122", -32.8908f, -68.8271f,
                                "Av. San Martín", "1020", deptoCapital, estadoComAceptado);
                Comercio c2 = buildComercio("COM-002", "HardMza Godoy Cruz", "2614441123", -32.9234f, -68.8412f,
                                "Av. San Martín Sur", "1540", deptoGodoyCruz, estadoComAceptado);
                Comercio c3 = buildComercio("COM-003", "HardMza Guaymallén", "2614441124", -32.8981f, -68.7915f,
                                "Acceso Este", "3280", deptoGuaymallen, estadoComAceptado);
                Comercio c4 = buildComercio("COM-004", "HardMza Maipú", "2614441125", -32.9801f, -68.7889f, "Pescara",
                                "250", deptoMaipu, estadoComAceptado);
                Comercio c5 = buildComercio("COM-005", "HardMza Las Heras", "2614441126", -32.8512f, -68.8310f,
                                "San Miguel", "1110", deptoLasHeras, estadoComAceptado);
                Comercio c6 = buildComercio("COM-006", "HardMza Luján", "2614441127", -33.0012f, -68.8712f, "Italia",
                                "5800", deptoLujan, estadoComAceptado);
                Comercio c7 = buildComercio("COM-007", "HardMza Express Centro", "2614441128", -32.8895f, -68.8451f,
                                "Peatonal Sarmiento", "145", deptoCapital, estadoComAceptado);

                vendedor.setComercios(new java.util.ArrayList<>(List.of(c1, c2, c3, c4, c5, c6, c7)));

                vendedor = (Vendedor) usuarioRepository.save(vendedor);
                vendedor.setUCodigo("VEN-" + vendedor.getId());
                usuarioRepository.save(vendedor);

                ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmo.getACNombre());
                strategy.generarYGuardarClave(vendedor, "vendedor1234");
                log.info("[DataSeeder] Vendedor de prueba y sus 7 Comercios creados.");
        }

        private Comercio buildComercio(String codigo, String nombre, String tel, float lat, float lon, String calle,
                        String nro, Departamento depto, EstadoComercio estado) {
                ComercioEstado comercioEstado = ComercioEstado.builder()
                                .CEFechaDesde(LocalDate.now())
                                .CEFechaHasta(null)
                                .estadoComercio(estado)
                                .build();
                return Comercio.builder()
                                .CCodigo(codigo)
                                .CNombreFantasia(nombre)
                                .CTelefono(tel)
                                .CLatitud(lat)
                                .CLongitud(lon)
                                .CDireccionCalle(calle)
                                .CNumeroEnCalle(nro)
                                .CFechaSolicitud(LocalDate.now())
                                .CFechaAlta(LocalDate.now())
                                .CFechaBaja(null)
                                .CHorarioAtencion("L a V de 9 a 18")
                                .departamento(depto)
                                .comercioEstados(new java.util.ArrayList<>(List.of(comercioEstado)))
                                .build();
        }

        private void seedCategoriasYProductos() {
                if (categoriaRepository.count() > 0) {
                        log.info("[DataSeeder] Categorías ya existen, se omite.");
                        return;
                }

                // Raíz 1: Componentes Internos
                Categoria catRoot1 = saveCat("CAT-ROOT-01", "Componentes Internos (Hardware)", null);

                Categoria catProc = saveCat("CAT-PROC", "Procesadores (CPU)", catRoot1);
                Categoria catProcAM4 = saveCat("CAT-PROC-AM4", "Procesadores Socket AM4", catProc);
                Categoria catProcAM5 = saveCat("CAT-PROC-AM5", "Procesadores Socket AM5", catProc);
                Categoria catProcLGA1700 = saveCat("CAT-PROC-LGA1700", "Procesadores Socket LGA1700", catProc);
                Categoria catProcLGA1200 = saveCat("CAT-PROC-LGA1200", "Procesadores Socket LGA1200", catProc);

                Categoria catMobo = saveCat("CAT-MOBO", "Placas Base (Motherboards)", catRoot1);
                Categoria catMoboAM4 = saveCat("CAT-MOBO-AM4", "Motherboards AM4", catMobo);
                Categoria catMoboAM5 = saveCat("CAT-MOBO-AM5", "Motherboards AM5", catMobo);
                Categoria catMoboLGA1700 = saveCat("CAT-MOBO-LGA1700", "Motherboards LGA1700", catMobo);

                Categoria catRam = saveCat("CAT-RAM", "Memorias RAM", catRoot1);
                Categoria catRamDimm = saveCat("CAT-RAM-DIMM", "Desktop (DIMM)", catRam);
                Categoria catRamSodimm = saveCat("CAT-RAM-SODIMM", "Notebook / Mini PC (SODIMM)", catRam);

                Categoria catStorage = saveCat("CAT-STORAGE", "Almacenamiento", catRoot1);
                Categoria catStorageM2 = saveCat("CAT-STORAGE-M2", "SSD M.2 NVMe", catStorage);
                Categoria catStorageSata = saveCat("CAT-STORAGE-SATA", "SSD SATA 2.5\"", catStorage);
                Categoria catStorageHdd = saveCat("CAT-STORAGE-HDD", "Discos Rígidos HDD", catStorage);

                Categoria catGpu = saveCat("CAT-GPU", "Placas de Video (GPU)", catRoot1);
                Categoria catGpuPcie4 = saveCat("CAT-GPU-PCIE4", "Placas de Video PCIe 4.0", catGpu);
                Categoria catGpuPcie3 = saveCat("CAT-GPU-PCIE3", "Placas de Video PCIe 3.0", catGpu);

                Categoria catPsu = saveCat("CAT-PSU", "Fuentes de Alimentación (PSU)", catRoot1);

                Categoria catCooling = saveCat("CAT-COOL", "Refrigeración", catRoot1);
                Categoria catCoolAir = saveCat("CAT-COOL-AIR", "Refrigeración por Aire", catCooling);
                Categoria catCoolLiquid = saveCat("CAT-COOL-LIQ", "Refrigeración Líquida (AIO)", catCooling);
                Categoria catCoolCase = saveCat("CAT-COOL-CASE", "Coolers para Gabinete", catCooling);

                // Raíz 2: Periféricos y Accesorios
                Categoria catRoot2 = saveCat("CAT-ROOT-02", "Periféricos y Accesorios", null);
                Categoria catMonitor = saveCat("CAT-PER-MON", "Monitores y Video", catRoot2);
                Categoria catKeyMouse = saveCat("CAT-PER-KM", "Teclados y Mouses", catRoot2);

                Categoria catAudio = saveCat("CAT-PER-AUD", "Audio", catRoot2);
                Categoria catAudioHead = saveCat("CAT-PER-AUD-HEAD", "Auriculares", catAudio);
                Categoria catAudioSpeak = saveCat("CAT-PER-AUD-SPK", "Parlantes", catAudio);
                Categoria catAudioMic = saveCat("CAT-PER-AUD-MIC", "Micrófonos", catAudio);

                Categoria catCable = saveCat("CAT-PER-CAB", "Cables y Adaptadores", catRoot2);
                Categoria catCableVideo = saveCat("CAT-PER-CAB-VID", "HDMI / DisplayPort", catCable);
                Categoria catCableUsb = saveCat("CAT-PER-CAB-USB", "USB / Type-C", catCable);
                Categoria catCablePwr = saveCat("CAT-PER-CAB-PWR", "Alimentación", catCable);

                // Raíz 3: Conectividad y Redes
                Categoria catRoot3 = saveCat("CAT-ROOT-03", "Conectividad y Redes", null);
                Categoria catNetRouter = saveCat("CAT-NET-ROUT", "Routers y Extensores", catRoot3);
                Categoria catNetCard = saveCat("CAT-NET-CARD", "Placas de Red (Wi-Fi / Bluetooth)", catRoot3);

                int prodCount = 1;
                prodCount = saveProduct(prodCount, "Procesador 6 Cores / 12 Threads Socket AM4",
                                Map.of("Socket", "AM4", "Cores", "6", "Threads", "12"), catProcAM4);
                prodCount = saveProduct(prodCount, "Procesador 8 Cores / 16 Threads Socket AM5",
                                Map.of("Socket", "AM5", "Cores", "8", "Threads", "16"), catProcAM5);
                prodCount = saveProduct(prodCount, "Procesador 10 Cores / 16 Threads Socket LGA1700",
                                Map.of("Socket", "LGA1700", "Cores", "10", "Threads", "16"), catProcLGA1700);
                prodCount = saveProduct(prodCount, "Procesador 4 Cores / 8 Threads Socket LGA1200",
                                Map.of("Socket", "LGA1200", "Cores", "4", "Threads", "8"), catProcLGA1200);

                prodCount = saveProduct(prodCount, "Motherboard Micro-ATX AM4 B450",
                                Map.of("Socket", "AM4", "Chipset", "B450", "Factor", "Micro-ATX"), catMoboAM4);
                prodCount = saveProduct(prodCount, "Motherboard ATX AM5 B650",
                                Map.of("Socket", "AM5", "Chipset", "B650", "Factor", "ATX"), catMoboAM5);
                prodCount = saveProduct(prodCount, "Motherboard Micro-ATX LGA1700 B760",
                                Map.of("Socket", "LGA1700", "Chipset", "B760", "Factor", "Micro-ATX"), catMoboLGA1700);

                prodCount = saveProduct(prodCount, "Memoria RAM 16GB DDR4 3200MHz",
                                Map.of("Tipo", "DDR4", "Capacidad", "16GB", "Frecuencia", "3200MHz"), catRamDimm);
                prodCount = saveProduct(prodCount, "Memoria RAM 8GB DDR4 2666MHz SODIMM",
                                Map.of("Tipo", "DDR4 SODIMM", "Capacidad", "8GB", "Frecuencia", "2666MHz"),
                                catRamSodimm);

                prodCount = saveProduct(prodCount, "SSD M.2 NVMe 1TB PCIe 4.0",
                                Map.of("Capacidad", "1TB", "Interfaz", "PCIe 4.0", "Formato", "M.2 2280"),
                                catStorageM2);
                prodCount = saveProduct(prodCount, "SSD SATA 2.5\" 500GB",
                                Map.of("Capacidad", "500GB", "Interfaz", "SATA III", "Formato", "2.5\""),
                                catStorageSata);
                prodCount = saveProduct(prodCount, "Disco Rígido HDD 2TB 7200RPM",
                                Map.of("Capacidad", "2TB", "Velocidad", "7200 RPM", "Formato", "3.5\""), catStorageHdd);

                prodCount = saveProduct(prodCount, "Placa de Video 8GB GDDR6 PCIe 4.0",
                                Map.of("Memoria", "8GB GDDR6", "Interfaz", "PCIe 4.0 x16"), catGpuPcie4);
                prodCount = saveProduct(prodCount, "Placa de Video 4GB GDDR5 PCIe 3.0",
                                Map.of("Memoria", "4GB GDDR5", "Interfaz", "PCIe 3.0 x16"), catGpuPcie3);

                prodCount = saveProduct(prodCount, "Fuente de Alimentación 650W 80 Plus Bronze",
                                Map.of("Potencia", "650W", "Certificación", "80 Plus Bronze"), catPsu);

                prodCount = saveProduct(prodCount, "Cooler para CPU por Aire 120mm RGB",
                                Map.of("Tipo", "Aire", "Ventilador", "120mm"), catCoolAir);
                prodCount = saveProduct(prodCount, "Refrigeración Líquida AIO 240mm",
                                Map.of("Tipo", "Líquida AIO", "Radiador", "240mm"), catCoolLiquid);
                prodCount = saveProduct(prodCount, "Cooler para Gabinete 120mm Silencioso",
                                Map.of("Tamaño", "120mm", "Nivel de Ruido", "20 dBA"), catCoolCase);

                prodCount = saveProduct(prodCount, "Monitor 24\" IPS 75Hz Full HD",
                                Map.of("Tamaño", "24\"", "Panel", "IPS", "Frecuencia", "75Hz"), catMonitor);
                prodCount = saveProduct(prodCount, "Kit Teclado y Mouse Inalámbricos",
                                Map.of("Conectividad", "Inalámbrica 2.4GHz"), catKeyMouse);

                prodCount = saveProduct(prodCount, "Auriculares Over-Ear con Micrófono",
                                Map.of("Diseño", "Over-Ear", "Micrófono", "Sí"), catAudioHead);
                prodCount = saveProduct(prodCount, "Parlantes Estéreo 2.0 USB",
                                Map.of("Canales", "2.0", "Potencia", "6W RMS"), catAudioSpeak);
                prodCount = saveProduct(prodCount, "Micrófono Condensador USB para Streaming",
                                Map.of("Tipo", "Condensador", "Conexión", "USB"), catAudioMic);

                prodCount = saveProduct(prodCount, "Cable HDMI 2.0 4K 2 Metros",
                                Map.of("Versión", "HDMI 2.0", "Longitud", "2m"), catCableVideo);
                prodCount = saveProduct(prodCount, "Cable USB a Type-C Carga Rápida",
                                Map.of("Conector A", "USB-A", "Conector B", "Type-C"), catCableUsb);
                prodCount = saveProduct(prodCount, "Cable de Alimentación Interlock 220V",
                                Map.of("Conector", "C13", "Corriente", "10A"), catCablePwr);

                prodCount = saveProduct(prodCount, "Router Dual-Band AC1200 4 Antenas",
                                Map.of("Banda", "Dual-Band", "Estándar", "Wi-Fi 5 (802.11ac)"), catNetRouter);
                prodCount = saveProduct(prodCount, "Placa de Red PCIe Wi-Fi 6 + Bluetooth 5.0",
                                Map.of("Interfaz", "PCIe", "Wi-Fi", "Wi-Fi 6", "Bluetooth", "5.0"), catNetCard);

                log.info("[DataSeeder] Taxonomía de Categorías y Productos creada.");
        }

        private Categoria saveCat(String codigo, String nombre, Categoria padre) {
                return categoriaRepository.save(Categoria.builder()
                                .CCodigo(codigo)
                                .CNombre(nombre)
                                .categoriaPadre(padre)
                                .CFechaAlta(LocalDate.now())
                                .build());
        }

        private int saveProduct(int currentCount, String nombre, Map<String, Object> especificaciones, Categoria cat) {
                String codigo = String.format("PROD-%03d", currentCount);
                Producto p = Producto.builder()
                                .PCodigo(codigo)
                                .PNombreTecnico(nombre)
                                .PEspecificaciones(especificaciones)
                                .PImagenUrl("https://via.placeholder.com/150")
                                .PFechaAlta(LocalDate.now())
                                .categoria(cat)
                                .build();
                productoRepository.save(p);
                return currentCount + 1;
        }

        private int crearNivelesParaCategoria(Categoria cat, int currentCounter) {
                NivelStock poco = NivelStock.builder()
                                .NSCodigo(String.format("NS-%03d", currentCounter++))
                                .NSNombre("Poco")
                                .NSCantidadDesde(1)
                                .NSCantidadHasta(5)
                                .NSFechaAlta(LocalDate.now())
                                .categoria(cat)
                                .build();

                NivelStock medio = NivelStock.builder()
                                .NSCodigo(String.format("NS-%03d", currentCounter++))
                                .NSNombre("Medio")
                                .NSCantidadDesde(6)
                                .NSCantidadHasta(15)
                                .NSFechaAlta(LocalDate.now())
                                .categoria(cat)
                                .build();

                NivelStock mucho = NivelStock.builder()
                                .NSCodigo(String.format("NS-%03d", currentCounter++))
                                .NSNombre("Mucho")
                                .NSCantidadDesde(16)
                                .NSCantidadHasta(100)
                                .NSFechaAlta(LocalDate.now())
                                .categoria(cat)
                                .build();

                nivelStockRepository.saveAll(List.of(poco, medio, mucho));
                return currentCounter;
        }

        private void seedNivelesStock() {
                if (nivelStockRepository.count() > 0) {
                        log.info("[DataSeeder] NivelesStock ya existen, se omite.");
                        return;
                }

                // Obtener todas las categorías que son hoja (las que no son categoría padre de ninguna otra)
                List<Categoria> todas = categoriaRepository.findAll();
                List<Categoria> categoriasHoja = todas.stream()
                                .filter(cat -> todas.stream().noneMatch(c -> cat.equals(c.getCategoriaPadre())))
                                .toList();

                int counter = 1;
                for (Categoria catHoja : categoriasHoja) {
                        counter = crearNivelesParaCategoria(catHoja, counter);
                }

                log.info("[DataSeeder] Se crearon los niveles de stock (Poco, Medio, Mucho) para las {} categorías hoja.", categoriasHoja.size());
        }

        private void seedConsultasStock() {
                consultaStockRepository.deleteAll();
                if (consultaStockRepository.count() > 0) {
                        log.info("[DataSeeder] ConsultasStock ya existen, se omiten.");
                        return;
                }

                EstadoConsultaStock estadoPendiente = estadoConsultaStockRepository
                                .findByECSNombreAndECSFechaBajaIsNull("StockPendiente")
                                .orElseThrow(() -> new IllegalStateException("Estado StockPendiente no encontrado"));
                EstadoConsultaStock estadoSinStock = estadoConsultaStockRepository
                                .findByECSNombreAndECSFechaBajaIsNull("SinStock")
                                .orElseThrow(() -> new IllegalStateException("Estado SinStock no encontrado"));
                EstadoConsultaStock estadoDisponible = estadoConsultaStockRepository
                                .findByECSNombreAndECSFechaBajaIsNull("StockDisponible")
                                .orElseThrow(() -> new IllegalStateException("Estado StockDisponible no encontrado"));

                Usuario uVendedor = usuarioRepository.findByUEmailAndUFechaBajaIsNull("vendedor@mendohard.com")
                                .orElseThrow(() -> new IllegalStateException("Vendedor de prueba no encontrado"));
                Vendedor vendedor = (Vendedor) uVendedor;
                Comercio comercio = vendedor.getComercios().stream()
                                .filter(c -> "COM-001".equals(c.getCCodigo())).findFirst()
                                .orElseThrow(() -> new IllegalStateException(
                                                "El comercio COM-001 no fue encontrado para el vendedor."));

                Usuario uConsumidor = usuarioRepository.findByUEmailAndUFechaBajaIsNull("consumidor@mendohard.com")
                                .orElseThrow(() -> new IllegalStateException("Consumidor de prueba no encontrado"));
                Consumidor consumidor = (Consumidor) uConsumidor;

                Producto producto = productoRepository.findByPCodigoAndPFechaBajaIsNull("PROD-001")
                                .orElseThrow(() -> new IllegalStateException("Producto PROD-001 no encontrado"));

                java.time.LocalDateTime expiracionPrueba = java.time.LocalDateTime.now().minusMinutes(3);

                ConsultaStock consulta1 = ConsultaStock.builder()
                                .CSContador(1L)
                                .CSFechaHoraSolicitud(java.time.LocalDateTime.now().minusHours(1))
                                .CSFechaHoraRespuesta(java.time.LocalDateTime.now().minusMinutes(30))
                                .CSFechaHoraExpiracion(expiracionPrueba)
                                .estadoConsultaStock(estadoPendiente)
                                .comercio(comercio)
                                .consumidor(consumidor)
                                .producto(producto)
                                .build();

                ConsultaStock consulta2 = ConsultaStock.builder()
                                .CSContador(2L)
                                .CSFechaHoraSolicitud(java.time.LocalDateTime.now().minusHours(1))
                                .CSFechaHoraRespuesta(java.time.LocalDateTime.now().minusMinutes(30))
                                .CSFechaHoraExpiracion(expiracionPrueba)
                                .estadoConsultaStock(estadoSinStock)
                                .comercio(comercio)
                                .consumidor(consumidor)
                                .producto(producto)
                                .build();

                ConsultaStock consulta3 = ConsultaStock.builder()
                                .CSContador(3L)
                                .CSFechaHoraSolicitud(java.time.LocalDateTime.now().minusHours(1))
                                .CSFechaHoraRespuesta(java.time.LocalDateTime.now().minusMinutes(30))
                                .CSFechaHoraExpiracion(expiracionPrueba)
                                .estadoConsultaStock(estadoDisponible)
                                .comercio(comercio)
                                .consumidor(consumidor)
                                .producto(producto)
                                .build();

                consultaStockRepository.saveAll(List.of(consulta1, consulta2, consulta3));
                log.info("[DataSeeder] 3 ConsultaStock de prueba creadas exitosamente para COM-001.");
        }
}
