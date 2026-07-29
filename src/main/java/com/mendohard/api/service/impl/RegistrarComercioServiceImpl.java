package com.mendohard.api.service.impl;

import com.mendohard.api.dto.ComercioResponseDTO;
import com.mendohard.api.dto.RegistrarComercioRequestDTO;
import com.mendohard.api.dto.UbicacionCascadaDTO;
import com.mendohard.api.exception.DatosNoValidosException;
import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.*;
import com.mendohard.api.repository.DepartamentoRepository;
import com.mendohard.api.repository.EstadoComercioRepository;
import com.mendohard.api.repository.VendedorRepository;
import com.mendohard.api.service.RegistrarComercioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrarComercioServiceImpl implements RegistrarComercioService {

    private final DepartamentoRepository departamentoRepository;
    private final EstadoComercioRepository estadoComercioRepository;
    private final VendedorRepository vendedorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UbicacionCascadaDTO> obtenerUbicacionesActivas() {
        List<Departamento> departamentos = departamentoRepository.findAllActivosCascade();
        
        return departamentos.stream().map(d -> {
            Provincia p = d.getProvincia();
            Pais pa = p.getPais();
            return UbicacionCascadaDTO.builder()
                    .departamentoCodigo(d.getDCodigo())
                    .departamentoNombre(d.getDNombre())
                    .provinciaCodigo(p.getProCodigo())
                    .provinciaNombre(p.getProNombre())
                    .paisCodigo(pa.getPCodigo())
                    .paisNombre(pa.getPNombre())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ComercioResponseDTO registrarComercio(RegistrarComercioRequestDTO request, String emailUsuario) {
        // 1. Sanitización
        String nombreFantasia = request.getNombreFantasia().trim();
        String telefono = request.getTelefono().trim();
        String direccionCalle = request.getDireccionCalle().trim();
        String numeroEnCalle = request.getNumeroEnCalle().trim();
        String horarioAtencion = request.getHorarioAtencion().trim();
        String departamentoCodigo = request.getDepartamentoCodigo().trim();

        // 2. Validación de Departamento
        Departamento departamento = departamentoRepository.findByDCodigoAndDFechaBajaIsNull(departamentoCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado o inactivo: " + departamentoCodigo));

        // 3. Obtención del Estado Inicial
        EstadoComercio estadoComercio = estadoComercioRepository.findByNombreActivo("ComercioPendiente")
                .orElseThrow(() -> new ResourceNotFoundException("Estado 'ComercioPendiente' no encontrado en el sistema"));

        // 4. Obtención del Vendedor activo
        Vendedor vendedor = vendedorRepository.findByEmailActivo(emailUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con email: " + emailUsuario));

        // 4.1 Validación de Estado del Vendedor
        boolean isAceptado = vendedor.getVendedorEstados() != null && vendedor.getVendedorEstados().stream()
                .anyMatch(ve -> ve.getVEFechaHasta() == null && "VendedorAceptado".equals(ve.getEstadoVendedor().getEVNombre()));

        if (!isAceptado) {
            throw new DatosNoValidosException("El vendedor debe estar en estado Aceptado para poder registrar nuevos comercios.");
        }

        // 5. Construcción de ComercioEstado
        ComercioEstado comercioEstado = ComercioEstado.builder()
                .CEFechaDesde(LocalDate.now())
                .estadoComercio(estadoComercio)
                .build();

        // 6. Generación de Código
        String codigoGenerado = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 7. Construcción de Comercio
        List<ComercioEstado> estados = new ArrayList<>();
        estados.add(comercioEstado);

        Comercio comercio = Comercio.builder()
                .CCodigo(codigoGenerado)
                .CNombreFantasia(nombreFantasia)
                .CTelefono(telefono)
                .CLatitud(request.getLatitud())
                .CLongitud(request.getLongitud())
                .CDireccionCalle(direccionCalle)
                .CNumeroEnCalle(numeroEnCalle)
                .CHorarioAtencion(horarioAtencion)
                .CFechaSolicitud(LocalDate.now())
                .departamento(departamento)
                .comercioEstados(estados)
                .build();

        // 8. Asociación y Persistencia a través de Vendedor
        if (vendedor.getComercios() == null) {
            vendedor.setComercios(new ArrayList<>());
        }
        vendedor.getComercios().add(comercio);
        vendedorRepository.save(vendedor);

        // 9. Mapeo a Response
        return ComercioResponseDTO.builder()
                .codigo(comercio.getCCodigo())
                .nombreFantasia(comercio.getCNombreFantasia())
                .telefono(comercio.getCTelefono())
                .direccionCalle(comercio.getCDireccionCalle())
                .numeroEnCalle(comercio.getCNumeroEnCalle())
                .horarioAtencion(comercio.getCHorarioAtencion())
                .fechaSolicitud(comercio.getCFechaSolicitud())
                .estadoActual(estadoComercio.getECNombre())
                .departamentoNombre(departamento.getDNombre())
                .provinciaNombre(departamento.getProvincia().getProNombre())
                .paisNombre(departamento.getProvincia().getPais().getPNombre())
                .build();
    }
}
