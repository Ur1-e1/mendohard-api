package com.mendohard.api.service.impl;

import com.mendohard.api.dto.ComercioVendedorResponseDTO;
import com.mendohard.api.dto.ConfirmarStockRequestDTO;
import com.mendohard.api.dto.ConsultaStockPendienteResponseDTO;
import com.mendohard.api.dto.ConsultaStockRespuestaDTO;
import com.mendohard.api.dto.NivelStockResponseDTO;
import com.mendohard.api.exception.DatosNoValidosException;
import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.*;
import com.mendohard.api.repository.*;
import com.mendohard.api.service.ConfirmarStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfirmarStockServiceImpl implements ConfirmarStockService {

    private final UsuarioRepository usuarioRepository;
    private final ComercioRepository comercioRepository;
    private final ConsultaStockRepository consultaStockRepository;
    private final EstadoConsultaStockRepository estadoConsultaStockRepository;
    private final NivelStockRepository nivelStockRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ComercioVendedorResponseDTO> listarComerciosVendedor() {
        Vendedor vendedor = getVendedorAutenticado();

        return vendedor.getComercios().stream()
                .filter(comercio -> comercio.getCFechaAlta() != null && comercio.getCFechaBaja() == null)
                .filter(this::tieneEstadoComercioAceptado)
                .map(comercio -> ComercioVendedorResponseDTO.builder()
                        .cCodigo(comercio.getCCodigo())
                        .cNombreFantasia(comercio.getCNombreFantasia())
                        .cTelefono(comercio.getCTelefono())
                        .cDireccionCalle(comercio.getCDireccionCalle())
                        .cNumeroEnCalle(comercio.getCNumeroEnCalle())
                        .cHorarioAtencion(comercio.getCHorarioAtencion())
                        .dNombre(comercio.getDepartamento().getDNombre())
                        .proNombre(comercio.getDepartamento().getProvincia().getProNombre())
                        .pNombre(comercio.getDepartamento().getProvincia().getPais().getPNombre())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultaStockPendienteResponseDTO> listarConsultasPendientes(String cCodigo) {
        Comercio comercio = comercioRepository.findByCCodigoAndCFechaBajaIsNull(cCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el Comercio con código: " + cCodigo));

        List<ConsultaStock> consultas = consultaStockRepository
                .findByComercio_CCodigoAndEstadoConsultaStock_ECSNombreAndEstadoConsultaStock_ECSFechaBajaIsNull(
                        cCodigo, "StockPendiente");

        return consultas.stream()
                .map(cs -> ConsultaStockPendienteResponseDTO.builder()
                        .csContador(cs.getCSContador())
                        .csFechaHoraSolicitud(cs.getCSFechaHoraSolicitud())
                        .csFechaHoraExpiracion(cs.getCSFechaHoraExpiracion())
                        .pNombreTecnico(cs.getProducto().getPNombreTecnico())
                        .pEspecificaciones(cs.getProducto().getPEspecificaciones())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NivelStockResponseDTO> obtenerNivelesStockJerarquicos(Long csContador) {
        ConsultaStock consultaStock = consultaStockRepository.findByCSContador(csContador)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la consulta de stock: " + csContador));

        Categoria categoriaHoja = consultaStock.getProducto().getCategoria();
        List<NivelStock> niveles = buscarNivelesStockRecursivo(categoriaHoja);

        if (niveles.isEmpty()) {
            throw new DatosNoValidosException("No se encontraron niveles de stock configurados para la categoría del producto ni en sus categorías padre.");
        }

        return niveles.stream()
                .map(ns -> NivelStockResponseDTO.builder()
                        .nsNombre(ns.getNSNombre())
                        .nsCantidadDesde(ns.getNSCantidadDesde())
                        .nsCantidadHasta(ns.getNSCantidadHasta())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConsultaStockRespuestaDTO confirmarStock(Long csContador, ConfirmarStockRequestDTO requestDTO) {
        ConsultaStock consultaStock = consultaStockRepository.findByCSContador(csContador)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la consulta de stock: " + csContador));

        if (!"StockPendiente".equals(consultaStock.getEstadoConsultaStock().getECSNombre())) {
            throw new DatosNoValidosException("La consulta no se encuentra en estado StockPendiente.");
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime vFechaHoraVencimiento;
        String nuevoEstadoNombre;

        if (requestDTO.getCsCantidadRespuesta() > 0) {
            // CASO A: Hay Stock (Camino Principal)
            if (requestDTO.getCsMarcasRespuesta() == null || requestDTO.getCsMarcasRespuesta().trim().isEmpty()) {
                throw new DatosNoValidosException("Debe proporcionar marcas cuando hay stock disponible.");
            }
            if (requestDTO.getCsPrecioRespuesta() == null || requestDTO.getCsPrecioRespuesta() <= 0) {
                throw new DatosNoValidosException("Debe proporcionar un precio válido (mayor a 0) cuando hay stock disponible.");
            }

            Categoria categoriaHoja = consultaStock.getProducto().getCategoria();
            List<NivelStock> niveles = buscarNivelesStockRecursivo(categoriaHoja);
            if (niveles.isEmpty()) {
                throw new DatosNoValidosException("No se encontraron niveles de stock configurados en la jerarquía.");
            }

            NivelStock nivelAplicable = niveles.stream()
                    .filter(ns -> ns.getNSCantidadDesde() <= requestDTO.getCsCantidadRespuesta() &&
                            (ns.getNSCantidadHasta() == null || ns.getNSCantidadHasta() >= requestDTO.getCsCantidadRespuesta()))
                    .findFirst()
                    .orElse(null);

            String nombreNivel = (nivelAplicable != null) ? nivelAplicable.getNSNombre() : "DEFAULT";

            if ("Poco".equalsIgnoreCase(nombreNivel)) {
                vFechaHoraVencimiento = ahora.plusHours(12);
            } else if ("Medio".equalsIgnoreCase(nombreNivel)) {
                vFechaHoraVencimiento = ahora.plusHours(24);
            } else if ("Mucho".equalsIgnoreCase(nombreNivel)) {
                vFechaHoraVencimiento = ahora.plusHours(48);
            } else {
                vFechaHoraVencimiento = ahora.plusHours(24);
            }

            nuevoEstadoNombre = "StockDisponible";
            consultaStock.setCSMarcasRespuesta(requestDTO.getCsMarcasRespuesta());
            consultaStock.setCSPrecioRespuesta(requestDTO.getCsPrecioRespuesta());

        } else {
            // CASO B: Sin Stock (CA N° 2)
            if (requestDTO.getCsMarcasRespuesta() != null && !requestDTO.getCsMarcasRespuesta().trim().isEmpty()) {
                throw new DatosNoValidosException("No debe proporcionar marcas cuando el stock es 0.");
            }
            if (requestDTO.getCsPrecioRespuesta() != null) {
                throw new DatosNoValidosException("No debe proporcionar precio cuando el stock es 0.");
            }

            vFechaHoraVencimiento = ahora.plusHours(24);
            nuevoEstadoNombre = "SinStock";
            consultaStock.setCSMarcasRespuesta(null);
            consultaStock.setCSPrecioRespuesta(null);
        }

        EstadoConsultaStock nuevoEstado = estadoConsultaStockRepository.findByECSNombreAndECSFechaBajaIsNull(nuevoEstadoNombre)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el estado de consulta de stock: " + nuevoEstadoNombre));

        consultaStock.setCSFechaHoraRespuesta(ahora);
        consultaStock.setCSDescripcionRespuesta(requestDTO.getCsDescripcionRespuesta());
        consultaStock.setCSCantidadRespuesta(requestDTO.getCsCantidadRespuesta());
        consultaStock.setCSFechaHoraExpiracion(vFechaHoraVencimiento);
        consultaStock.setEstadoConsultaStock(nuevoEstado);

        consultaStockRepository.save(consultaStock);

        return ConsultaStockRespuestaDTO.builder()
                .csContador(consultaStock.getCSContador())
                .estado(nuevoEstado.getECSNombre())
                .csFechaHoraRespuesta(consultaStock.getCSFechaHoraRespuesta())
                .csFechaHoraExpiracion(consultaStock.getCSFechaHoraExpiracion())
                .csMarcasRespuesta(consultaStock.getCSMarcasRespuesta())
                .csDescripcionRespuesta(consultaStock.getCSDescripcionRespuesta())
                .csPrecioRespuesta(consultaStock.getCSPrecioRespuesta())
                .csCantidadRespuesta(consultaStock.getCSCantidadRespuesta())
                .build();
    }

    private List<NivelStock> buscarNivelesStockRecursivo(Categoria categoria) {
        if (categoria == null) {
            return List.of();
        }
        List<NivelStock> niveles = nivelStockRepository.findByCategoria_IdAndNSFechaBajaIsNull(categoria.getId());
        if (!niveles.isEmpty()) {
            return niveles;
        }
        return buscarNivelesStockRecursivo(categoria.getCategoriaPadre());
    }

    private Vendedor getVendedorAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Usuario no autenticado");
        }
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByUEmailAndUFechaBajaIsNull(email)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario con email: " + email));
                
        if (!(usuario instanceof Vendedor)) {
            throw new DatosNoValidosException("El usuario logueado no es un Vendedor.");
        }
        return (Vendedor) usuario;
    }

    private boolean tieneEstadoComercioAceptado(Comercio comercio) {
        if (comercio.getComercioEstados() == null) {
            return false;
        }
        return comercio.getComercioEstados().stream()
                .anyMatch(ce -> ce.getCEFechaHasta() == null
                        && ce.getEstadoComercio().getECNombre().equals("ComercioAceptado")
                        && ce.getEstadoComercio().getECFechaBaja() == null);
    }
}
