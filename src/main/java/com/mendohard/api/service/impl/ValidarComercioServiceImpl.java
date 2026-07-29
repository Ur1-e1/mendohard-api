package com.mendohard.api.service.impl;

import com.mendohard.api.dto.ComercioDetalleValidacionDto;
import com.mendohard.api.dto.ComercioPendienteResponseDto;
import com.mendohard.api.dto.DecisionValidacionRequestDto;
import com.mendohard.api.exception.DatosNoValidosException;
import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.Comercio;
import com.mendohard.api.model.ComercioEstado;
import com.mendohard.api.model.EstadoComercio;
import com.mendohard.api.model.Vendedor;
import com.mendohard.api.repository.ComercioRepository;
import com.mendohard.api.repository.EstadoComercioRepository;
import com.mendohard.api.repository.VendedorRepository;
import com.mendohard.api.service.ValidarComercioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidarComercioServiceImpl implements ValidarComercioService {

    private final ComercioRepository comercioRepository;
    private final VendedorRepository vendedorRepository;
    private final EstadoComercioRepository estadoComercioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ComercioPendienteResponseDto> consultarComerciosPendientes() {
        List<Comercio> comerciosPendientes = comercioRepository.findComerciosPendientesDeVendedoresAceptados();

        return comerciosPendientes.stream().map(comercio -> {
            Vendedor vendedor = vendedorRepository.findByComercioId(comercio.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendedor asociado al comercio no encontrado"));

            return ComercioPendienteResponseDto.builder()
                    // Datos Vendedor
                    .uCodigo(vendedor.getUCodigo())
                    .uNombre(vendedor.getUNombre())
                    .uApellido(vendedor.getUApellido())
                    .uEmail(vendedor.getUEmail())
                    .vTelefono(vendedor.getVTelefono())
                    .vCuit(vendedor.getVCuit())
                    .vRazonSocial(vendedor.getVRazonSocial())
                    .vCategoriaFiscal(vendedor.getVCategoriaFiscal())
                    
                    // Datos Comercio
                    .cCodigo(comercio.getCCodigo())
                    .cNombreFantasia(comercio.getCNombreFantasia())
                    .cTelefono(comercio.getCTelefono())
                    .cLatitud(comercio.getCLatitud())
                    .cLongitud(comercio.getCLongitud())
                    .cDireccionCalle(comercio.getCDireccionCalle())
                    .cNumeroEnCalle(comercio.getCNumeroEnCalle())
                    .cFechaSolicitud(comercio.getCFechaSolicitud())
                    .cHorarioAtencion(comercio.getCHorarioAtencion())
                    
                    // Ubicación
                    .dNombre(comercio.getDepartamento().getDNombre())
                    .proNombre(comercio.getDepartamento().getProvincia().getProNombre())
                    .pNombre(comercio.getDepartamento().getProvincia().getPais().getPNombre())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ComercioDetalleValidacionDto obtenerComercioParaValidacion(String cCodigo) {
        Comercio comercio = comercioRepository.findByCodigoComercio(cCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Comercio no encontrado"));

        verificarEstadoPendiente(comercio);

        return ComercioDetalleValidacionDto.builder()
                .cCodigo(comercio.getCCodigo())
                .cNombreFantasia(comercio.getCNombreFantasia())
                .build();
    }

    @Override
    @Transactional
    public void procesarDecisionValidacion(String cCodigo, DecisionValidacionRequestDto requestDto) {
        String decision = requestDto.getDecisionValidacion() != null ? requestDto.getDecisionValidacion().trim() : "";
        
        if (!decision.equalsIgnoreCase("Aceptar") && !decision.equalsIgnoreCase("Rechazar")) {
            throw new DatosNoValidosException("Opción ingresada no válida");
        }

        Comercio comercio = comercioRepository.findByCodigoComercio(cCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Comercio no encontrado"));

        verificarEstadoPendiente(comercio);

        // Cerrar estado actual
        ComercioEstado estadoActual = comercio.getComercioEstados().stream()
                .filter(ce -> ce.getCEFechaHasta() == null)
                .findFirst()
                .orElseThrow(() -> new DatosNoValidosException("No se encontró estado activo para el comercio"));
        
        estadoActual.setCEFechaHasta(LocalDate.now());

        // Crear nuevo estado
        String nombreNuevoEstado = decision.equalsIgnoreCase("Aceptar") ? "ComercioAceptado" : "ComercioRechazado";
        EstadoComercio nuevoEstadoComercio = estadoComercioRepository.findByNombreActivo(nombreNuevoEstado)
                .orElseThrow(() -> new ResourceNotFoundException("Estado " + nombreNuevoEstado + " no encontrado"));

        ComercioEstado nuevoEstado = ComercioEstado.builder()
                .CEFechaDesde(LocalDate.now())
                .CEFechaHasta(null)
                .estadoComercio(nuevoEstadoComercio)
                .build();

        comercio.getComercioEstados().add(nuevoEstado);
        comercio.setCFechaAlta(LocalDate.now());

        if (decision.equalsIgnoreCase("Rechazar")) {
            comercio.setCFechaBaja(LocalDate.now());
        }

        comercioRepository.save(comercio);
    }

    private void verificarEstadoPendiente(Comercio comercio) {
        boolean esPendiente = comercio.getComercioEstados().stream()
                .anyMatch(ce -> ce.getCEFechaHasta() == null &&
                        "ComercioPendiente".equals(ce.getEstadoComercio().getECNombre()) &&
                        ce.getEstadoComercio().getECFechaBaja() == null);
        
        if (!esPendiente) {
            throw new DatosNoValidosException("El comercio no se encuentra en estado pendiente");
        }
    }
}
