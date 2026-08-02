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
                seedVendedorPendienteYComercio();
                seedVendedorAceptadoYComercioPendiente();
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
                                                "Permite al vendedor confirmar el stock de los productos"));
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
                                                buildRolPermiso(pConfirmarStock)))
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
                                                buildRolPermiso(pAbmProducto)))
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

        private void seedNivelesStock() {
                if (nivelStockRepository.count() > 0) {
                        log.info("[DataSeeder] NivelesStock ya existen, se omiten.");
                        return;
                }
                com.mendohard.api.model.Categoria categoria = categoriaRepository.findByCCodigo("CAT-003")
                                .orElseThrow(() -> new IllegalStateException(
                                                "Categoría 'Desktop (DIMM)' (CAT-003) no encontrada"));

                List<NivelStock> niveles = List.of(
                                NivelStock.builder().NSCodigo("NS-001").NSNombre("Stock Bajo").NSCantidadDesde(1)
                                                .NSCantidadHasta(5).NSFechaAlta(LocalDate.now()).categoria(categoria)
                                                .build(),
                                NivelStock.builder().NSCodigo("NS-002").NSNombre("Stock Medio").NSCantidadDesde(6)
                                                .NSCantidadHasta(15).NSFechaAlta(LocalDate.now()).categoria(categoria)
                                                .build(),
                                NivelStock.builder().NSCodigo("NS-003").NSNombre("Stock Alto").NSCantidadDesde(16)
                                                .NSCantidadHasta(100).NSFechaAlta(LocalDate.now()).categoria(categoria)
                                                .build());
                nivelStockRepository.saveAll(niveles);
                log.info("[DataSeeder] {} NivelesStock creados.", niveles.size());
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

                // Estado Vendedor
                EstadoVendedor estadoAceptado = estadoVendedorRepository.findByNombreActivo("VendedorAceptado")
                                .orElseThrow(() -> new IllegalStateException("Estado VendedorAceptado no encontrado"));

                VendedorEstado vendedorEstado = VendedorEstado.builder()
                                .VEFechaDesde(LocalDate.now())
                                .VEFechaHasta(null)
                                .estadoVendedor(estadoAceptado)
                                .build();
                vendedor.setVendedorEstados(new java.util.ArrayList<>(List.of(vendedorEstado)));

                // Comercio asociado
                Departamento depto = departamentoRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("No hay departamentos cargados"));

                EstadoComercio estadoComAceptado = estadoComercioRepository.findByNombreActivo("ComercioAceptado")
                                .orElseThrow(() -> new IllegalStateException("Estado ComercioAceptado no encontrado"));

                ComercioEstado comercioEstado = ComercioEstado.builder()
                                .CEFechaDesde(LocalDate.now())
                                .CEFechaHasta(null)
                                .estadoComercio(estadoComAceptado)
                                .build();

                Comercio comercio = Comercio.builder()
                                .CCodigo("TEMP-COM")
                                .CNombreFantasia("HardMza")
                                .CTelefono("2614441122")
                                .CLatitud(-32.89084f)
                                .CLongitud(-68.82717f)
                                .CDireccionCalle("San Martín")
                                .CNumeroEnCalle("1020")
                                .CFechaSolicitud(LocalDate.now())
                                .CFechaAlta(LocalDate.now())
                                .CFechaBaja(null)
                                .CHorarioAtencion("L a V de 9 a 18")
                                .departamento(depto)
                                .comercioEstados(new java.util.ArrayList<>(List.of(comercioEstado)))
                                .build();

                vendedor.setComercios(new java.util.ArrayList<>(List.of(comercio)));

                vendedor = (Vendedor) usuarioRepository.save(vendedor);
                vendedor.setUCodigo("VEN-" + vendedor.getId());
                vendedor.getComercios().get(0).setCCodigo("COM-" + vendedor.getComercios().get(0).getId());
                usuarioRepository.save(vendedor);

                ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmo.getACNombre());
                strategy.generarYGuardarClave(vendedor, "vendedor1234");
                log.info("[DataSeeder] Vendedor de prueba y su Comercio creados.");
        }

        private void seedVendedorPendienteYComercio() {
                if (usuarioRepository.existsByUEmailActivo("vendedor_pendiente@mendohard.com")) {
                        log.info("[DataSeeder] Vendedor pendiente ya existe, se omite.");
                        return;
                }

                AlgoritmoClave algoritmo = algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema")
                                .orElseThrow(() -> new IllegalStateException("AlgoritmoClave no encontrado"));
                Rol rolVendedor = rolRepository.findByRNombreActivo("Vendedor")
                                .orElseThrow(() -> new IllegalStateException("Rol Vendedor no encontrado"));

                Vendedor vendedor = Vendedor.builder()
                                .UCodigo("TEMP-VP")
                                .UNombre("Carlos")
                                .UApellido("Pendiente")
                                .UEmail("vendedor_pendiente@mendohard.com")
                                .VTelefono("2615559999")
                                .VCuit("27309999999")
                                .VRazonSocial("Pendiente Tech SRL")
                                .VCategoriaFiscal("Responsable Inscripto")
                                .UFechaAlta(LocalDate.now())
                                .UFechaBaja(null)
                                .rol(rolVendedor)
                                .algoritmoClave(algoritmo)
                                .build();

                // Estado Vendedor
                EstadoVendedor estadoPendiente = estadoVendedorRepository.findByNombreActivo("VendedorPendiente")
                                .orElseThrow(() -> new IllegalStateException("Estado VendedorPendiente no encontrado"));

                VendedorEstado vendedorEstado = VendedorEstado.builder()
                                .VEFechaDesde(LocalDate.now())
                                .VEFechaHasta(null)
                                .estadoVendedor(estadoPendiente)
                                .build();
                vendedor.setVendedorEstados(new java.util.ArrayList<>(List.of(vendedorEstado)));

                // Comercio asociado
                Departamento depto = departamentoRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("No hay departamentos cargados"));

                EstadoComercio estadoComPendiente = estadoComercioRepository.findByNombreActivo("ComercioPendiente")
                                .orElseThrow(() -> new IllegalStateException("Estado ComercioPendiente no encontrado"));

                ComercioEstado comercioEstado = ComercioEstado.builder()
                                .CEFechaDesde(LocalDate.now())
                                .CEFechaHasta(null)
                                .estadoComercio(estadoComPendiente)
                                .build();

                Comercio comercio = Comercio.builder()
                                .CCodigo("TEMP-COMP")
                                .CNombreFantasia("HardPendiente")
                                .CTelefono("2614449999")
                                .CLatitud(-32.89000f)
                                .CLongitud(-68.82000f)
                                .CDireccionCalle("San Martín")
                                .CNumeroEnCalle("9999")
                                .CFechaSolicitud(LocalDate.now())
                                .CFechaAlta(null) // Todavía no aceptado
                                .CFechaBaja(null)
                                .CHorarioAtencion("L a V de 9 a 18")
                                .departamento(depto)
                                .comercioEstados(new java.util.ArrayList<>(List.of(comercioEstado)))
                                .build();

                vendedor.setComercios(new java.util.ArrayList<>(List.of(comercio)));

                vendedor = (Vendedor) usuarioRepository.save(vendedor);
                vendedor.setUCodigo("VEN-" + vendedor.getId());
                vendedor.getComercios().get(0).setCCodigo("COM-" + vendedor.getComercios().get(0).getId());
                usuarioRepository.save(vendedor);

                ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmo.getACNombre());
                strategy.generarYGuardarClave(vendedor, "vendedor1234");
                log.info("[DataSeeder] Vendedor PENDIENTE de prueba y su Comercio creados.");
        }

        private void seedCategoriasYProductos() {
                if (categoriaRepository.count() > 0) {
                        log.info("[DataSeeder] Categorías ya existen, se omiten.");
                        return;
                }

                // Jerarquía de Categorías
                Categoria hardware = Categoria.builder()
                                .CCodigo("CAT-001")
                                .CNombre("Hardware")
                                .CFechaAlta(LocalDate.now())
                                .build();
                hardware = categoriaRepository.save(hardware);

                Categoria memorias = Categoria.builder()
                                .CCodigo("CAT-002")
                                .CNombre("Memorias RAM")
                                .categoriaPadre(hardware)
                                .CFechaAlta(LocalDate.now())
                                .build();
                memorias = categoriaRepository.save(memorias);

                Categoria desktopDimm = Categoria.builder()
                                .CCodigo("CAT-003")
                                .CNombre("Desktop (DIMM)")
                                .categoriaPadre(memorias)
                                .CFechaAlta(LocalDate.now())
                                .build();
                desktopDimm = categoriaRepository.save(desktopDimm);

                // Productos
                Producto ramActiva = Producto.builder()
                                .PCodigo("PROD-001")
                                .PNombreTecnico("Memoria RAM Kingston Fury Beast 16GB")
                                .PEspecificaciones(Map.of("Tipo", "DDR4", "Capacidad", "16GB", "Velocidad", "3200MHz"))
                                .PImagenUrl("https://example.com/ram-16gb.jpg")
                                .PFechaAlta(LocalDate.now())
                                .categoria(desktopDimm)
                                .build();
                productoRepository.save(ramActiva);

                Producto ramInactiva = Producto.builder()
                                .PCodigo("PROD-002")
                                .PNombreTecnico("Memoria RAM Genérica 4GB (Descontinuada)")
                                .PEspecificaciones(Map.of("Tipo", "DDR3", "Capacidad", "4GB"))
                                .PImagenUrl("https://example.com/ram-4gb.jpg")
                                .PFechaAlta(LocalDate.now().minusYears(1))
                                .PFechaBaja(LocalDate.now())
                                .categoria(desktopDimm)
                                .build();
                productoRepository.save(ramInactiva);

                log.info("[DataSeeder] Categorías y Productos de prueba creados.");
        }

        private void seedVendedorAceptadoYComercioPendiente() {
                if (usuarioRepository.existsByUEmailActivo("vendedor_aceptado@mendohard.com")) {
                        log.info("[DataSeeder] Vendedor aceptado con comercio pendiente ya existe, se omite.");
                        return;
                }

                AlgoritmoClave algoritmo = algoritmoClaveRepository.findByACNombreActivo("ContraseñaEnSistema")
                                .orElseThrow(() -> new IllegalStateException("AlgoritmoClave no encontrado"));
                Rol rolVendedor = rolRepository.findByRNombreActivo("Vendedor")
                                .orElseThrow(() -> new IllegalStateException("Rol Vendedor no encontrado"));

                Vendedor vendedor = Vendedor.builder()
                                .UCodigo("TEMP-VA")
                                .UNombre("Esteban")
                                .UApellido("Trabajo")
                                .UEmail("vendedor_aceptado@mendohard.com")
                                .VTelefono("2617778888")
                                .VCuit("20307778889")
                                .VRazonSocial("Aceptado Tech SRL")
                                .VCategoriaFiscal("Monotributista")
                                .UFechaAlta(LocalDate.now())
                                .UFechaBaja(null)
                                .rol(rolVendedor)
                                .algoritmoClave(algoritmo)
                                .build();

                // Estado Vendedor: Aceptado
                EstadoVendedor estadoAceptado = estadoVendedorRepository.findByNombreActivo("VendedorAceptado")
                                .orElseThrow(() -> new IllegalStateException("Estado VendedorAceptado no encontrado"));

                VendedorEstado vendedorEstado = VendedorEstado.builder()
                                .VEFechaDesde(LocalDate.now())
                                .VEFechaHasta(null)
                                .estadoVendedor(estadoAceptado)
                                .build();
                vendedor.setVendedorEstados(new java.util.ArrayList<>(List.of(vendedorEstado)));

                // Comercio asociado: Pendiente
                Departamento depto = departamentoRepository.findAll().stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException("No hay departamentos cargados"));

                EstadoComercio estadoComPendiente = estadoComercioRepository.findByNombreActivo("ComercioPendiente")
                                .orElseThrow(() -> new IllegalStateException("Estado ComercioPendiente no encontrado"));

                ComercioEstado comercioEstado = ComercioEstado.builder()
                                .CEFechaDesde(LocalDate.now())
                                .CEFechaHasta(null)
                                .estadoComercio(estadoComPendiente)
                                .build();

                Comercio comercio = Comercio.builder()
                                .CCodigo("TEMP-COMP-2")
                                .CNombreFantasia("HardPendienteVal")
                                .CTelefono("2614448888")
                                .CLatitud(-32.89010f)
                                .CLongitud(-68.82010f)
                                .CDireccionCalle("San Martín Sur")
                                .CNumeroEnCalle("8888")
                                .CFechaSolicitud(LocalDate.now())
                                .CFechaAlta(null) // Todavía no aceptado
                                .CFechaBaja(null)
                                .CHorarioAtencion("L a V de 9 a 18")
                                .departamento(depto)
                                .comercioEstados(new java.util.ArrayList<>(List.of(comercioEstado)))
                                .build();

                vendedor.setComercios(new java.util.ArrayList<>(List.of(comercio)));

                vendedor = (Vendedor) usuarioRepository.save(vendedor);
                vendedor.setUCodigo("VEN-" + vendedor.getId());
                vendedor.getComercios().get(0).setCCodigo("COM-" + vendedor.getComercios().get(0).getId());
                usuarioRepository.save(vendedor);

                ClaveStrategy strategy = claveStrategyFactory.getStrategy(algoritmo.getACNombre());
                strategy.generarYGuardarClave(vendedor, "vendedor1234");
                log.info("[DataSeeder] Vendedor ACEPTADO de prueba y su Comercio PENDIENTE creados.");
        }

        private void seedConsultasStock() {
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
                Comercio comercio = vendedor.getComercios().stream().findFirst()
                                .orElseThrow(() -> new IllegalStateException(
                                                "El vendedor de prueba no tiene comercios"));

                Usuario uConsumidor = usuarioRepository.findByUEmailAndUFechaBajaIsNull("consumidor@mendohard.com")
                                .orElseThrow(() -> new IllegalStateException("Consumidor de prueba no encontrado"));
                Consumidor consumidor = (Consumidor) uConsumidor;

                Producto producto = productoRepository.findByPCodigoAndPFechaBajaIsNull("PROD-001")
                                .orElseThrow(() -> new IllegalStateException("Producto PROD-001 no encontrado"));

                java.time.LocalDateTime expiracionPrueba = java.time.LocalDateTime.now().minusMinutes(3);

                ConsultaStock consulta1 = ConsultaStock.builder()
                                .CSContador(1L)
                                .CSFechaHoraSolicitud(java.time.LocalDateTime.now().minusHours(1))
                                .CSFechaHoraExpiracion(expiracionPrueba)
                                .estadoConsultaStock(estadoPendiente)
                                .comercio(comercio)
                                .consumidor(consumidor)
                                .producto(producto)
                                .build();

                ConsultaStock consulta2 = ConsultaStock.builder()
                                .CSContador(2L)
                                .CSFechaHoraSolicitud(java.time.LocalDateTime.now().minusHours(1))
                                .CSFechaHoraExpiracion(expiracionPrueba)
                                .estadoConsultaStock(estadoSinStock)
                                .comercio(comercio)
                                .consumidor(consumidor)
                                .producto(producto)
                                .build();

                ConsultaStock consulta3 = ConsultaStock.builder()
                                .CSContador(3L)
                                .CSFechaHoraSolicitud(java.time.LocalDateTime.now().minusHours(1))
                                .CSFechaHoraExpiracion(expiracionPrueba)
                                .estadoConsultaStock(estadoDisponible)
                                .comercio(comercio)
                                .consumidor(consumidor)
                                .producto(producto)
                                .build();

                consultaStockRepository.saveAll(List.of(consulta1, consulta2, consulta3));
                log.info("[DataSeeder] 3 ConsultaStock de prueba (vencidas hace 3 min) creadas exitosamente.");
        }
}
