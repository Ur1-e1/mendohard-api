package com.mendohard.api.service.impl;

import com.mendohard.api.dto.ValidarVendedorRequestDTO;
import com.mendohard.api.dto.VendedorDetalleResponseDTO;
import com.mendohard.api.dto.VendedorPendienteResponseDTO;
import com.mendohard.api.exception.DatosNoValidosException;
import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.Comercio;
import com.mendohard.api.model.ComercioEstado;
import com.mendohard.api.model.EstadoComercio;
import com.mendohard.api.model.EstadoVendedor;
import com.mendohard.api.model.Vendedor;
import com.mendohard.api.model.VendedorEstado;
import com.mendohard.api.repository.EstadoComercioRepository;
import com.mendohard.api.repository.EstadoVendedorRepository;
import com.mendohard.api.repository.VendedorRepository;
import com.mendohard.api.service.ValidarVendedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidarVendedorServiceImpl implements ValidarVendedorService {

    private final VendedorRepository vendedorRepository;
    private final EstadoVendedorRepository estadoVendedorRepository;
    private final EstadoComercioRepository estadoComercioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VendedorPendienteResponseDTO> obtenerVendedoresPendientes() {
        return vendedorRepository.findVendedoresPendientes().stream()
                .map(v -> VendedorPendienteResponseDTO.builder()
                        .uCodigo(v.getUCodigo())
                        .uNombre(v.getUNombre())
                        .uApellido(v.getUApellido())
                        .uEmail(v.getUEmail())
                        .vTelefono(v.getVTelefono())
                        .vCuit(v.getVCuit())
                        .vRazonSocial(v.getVRazonSocial())
                        .vCategoriaFiscal(v.getVCategoriaFiscal())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VendedorDetalleResponseDTO obtenerDetalleVendedorPendiente(String uCodigo) {
        Vendedor vendedor = vendedorRepository.findByUCodigoAndUFechaBajaIsNull(uCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor activo no encontrado con código: " + uCodigo));

        if (vendedor.getComercios() == null || vendedor.getComercios().isEmpty()) {
            throw new ResourceNotFoundException("El vendedor no tiene un comercio asociado.");
        }

        Comercio comercio = vendedor.getComercios().get(0);

        return VendedorDetalleResponseDTO.builder()
                .uCodigo(vendedor.getUCodigo())
                .uNombre(vendedor.getUNombre())
                .uApellido(vendedor.getUApellido())
                .uEmail(vendedor.getUEmail())
                .vTelefono(vendedor.getVTelefono())
                .vCuit(vendedor.getVCuit())
                .vRazonSocial(vendedor.getVRazonSocial())
                .vCategoriaFiscal(vendedor.getVCategoriaFiscal())
                // Datos del Comercio
                .cNombreFantasia(comercio.getCNombreFantasia())
                .cTelefono(comercio.getCTelefono())
                .cLatitud(comercio.getCLatitud())
                .cLongitud(comercio.getCLongitud())
                .cDireccionCalle(comercio.getCDireccionCalle())
                .cNumeroEnCalle(comercio.getCNumeroEnCalle())
                .cFechaSolicitud(comercio.getCFechaSolicitud())
                .cHorarioAtencion(comercio.getCHorarioAtencion())
                // Datos Geográficos
                .dNombre(comercio.getDepartamento().getDNombre())
                .proNombre(comercio.getDepartamento().getProvincia().getProNombre())
                .pNombre(comercio.getDepartamento().getProvincia().getPais().getPNombre())
                .build();
    }

    @Override
    @Transactional
    public void validarVendedor(ValidarVendedorRequestDTO request) {
        String decision = request.getDecisionValidacion().trim();
        boolean isAceptar = decision.equalsIgnoreCase("Aceptar");
        boolean isRechazar = decision.equalsIgnoreCase("Rechazar");

        if (!isAceptar && !isRechazar) {
            throw new DatosNoValidosException("La decisión debe ser 'Aceptar' o 'Rechazar'.");
        }

        Vendedor vendedor = vendedorRepository.findByUCodigoAndUFechaBajaIsNull(request.getUCodigo())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor activo no encontrado con código: " + request.getUCodigo()));

        if (vendedor.getComercios() == null || vendedor.getComercios().isEmpty()) {
            throw new ResourceNotFoundException("El vendedor no tiene un comercio asociado.");
        }
        Comercio comercio = vendedor.getComercios().get(0);

        if (isAceptar) {
            aceptarVendedor(vendedor, comercio);
        } else {
            rechazarVendedor(vendedor, comercio);
        }
    }

    private void aceptarVendedor(Vendedor vendedor, Comercio comercio) {
        LocalDate hoy = LocalDate.now();

        // 1. Modificar VendedorEstado actual
        VendedorEstado veActual = obtenerVendedorEstadoActivo(vendedor);
        veActual.setVEFechaHasta(hoy);

        // 2. Nuevo VendedorEstado
        EstadoVendedor evAceptado = estadoVendedorRepository.findByNombreActivo("VendedorAceptado")
                .orElseThrow(() -> new ResourceNotFoundException("Estado VendedorAceptado no encontrado"));
        
        VendedorEstado veNuevo = VendedorEstado.builder()
                .VEFechaDesde(hoy)
                .VEFechaHasta(null)
                .estadoVendedor(evAceptado)
                .build();
        vendedor.getVendedorEstados().add(veNuevo);

        // 3. Modificar ComercioEstado actual
        ComercioEstado ceActual = obtenerComercioEstadoActivo(comercio);
        ceActual.setCEFechaHasta(hoy);

        // 4. Nuevo ComercioEstado
        EstadoComercio ecAceptado = estadoComercioRepository.findByNombreActivo("ComercioAceptado")
                .orElseThrow(() -> new ResourceNotFoundException("Estado ComercioAceptado no encontrado"));

        ComercioEstado ceNuevo = ComercioEstado.builder()
                .CEFechaDesde(hoy)
                .CEFechaHasta(null)
                .estadoComercio(ecAceptado)
                .build();
        comercio.getComercioEstados().add(ceNuevo);
        
        // 5. Modificar Comercio
        comercio.setCFechaAlta(hoy);
    }

    private void rechazarVendedor(Vendedor vendedor, Comercio comercio) {
        LocalDate hoy = LocalDate.now();

        // 1. Modificar VendedorEstado actual
        VendedorEstado veActual = obtenerVendedorEstadoActivo(vendedor);
        veActual.setVEFechaHasta(hoy);

        // 2. Nuevo VendedorEstado
        EstadoVendedor evRechazado = estadoVendedorRepository.findByNombreActivo("VendedorRechazado")
                .orElseThrow(() -> new ResourceNotFoundException("Estado VendedorRechazado no encontrado"));
        
        VendedorEstado veNuevo = VendedorEstado.builder()
                .VEFechaDesde(hoy)
                .VEFechaHasta(null)
                .estadoVendedor(evRechazado)
                .build();
        vendedor.getVendedorEstados().add(veNuevo);

        // 3. Modificar Vendedor
        vendedor.setUFechaBaja(hoy);

        // 4. Modificar ComercioEstado actual
        ComercioEstado ceActual = obtenerComercioEstadoActivo(comercio);
        ceActual.setCEFechaHasta(hoy);

        // 5. Nuevo ComercioEstado
        EstadoComercio ecRechazado = estadoComercioRepository.findByNombreActivo("ComercioRechazado")
                .orElseThrow(() -> new ResourceNotFoundException("Estado ComercioRechazado no encontrado"));

        ComercioEstado ceNuevo = ComercioEstado.builder()
                .CEFechaDesde(hoy)
                .CEFechaHasta(null)
                .estadoComercio(ecRechazado)
                .build();
        comercio.getComercioEstados().add(ceNuevo);

        // 6. Modificar Comercio
        comercio.setCFechaAlta(hoy);
        comercio.setCFechaBaja(hoy);
    }

    private VendedorEstado obtenerVendedorEstadoActivo(Vendedor vendedor) {
        return vendedor.getVendedorEstados().stream()
                .filter(ve -> ve.getVEFechaHasta() == null)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("El vendedor no tiene un estado activo."));
    }

    private ComercioEstado obtenerComercioEstadoActivo(Comercio comercio) {
        return comercio.getComercioEstados().stream()
                .filter(ce -> ce.getCEFechaHasta() == null)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("El comercio no tiene un estado activo."));
    }
}
