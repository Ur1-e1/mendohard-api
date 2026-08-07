package com.mendohard.api.service.impl;

import com.mendohard.api.service.InhabilitarUsuarioService;
import com.mendohard.api.dto.ConsumidorInhabilitarResponseDto;
import com.mendohard.api.dto.VendedorInhabilitarResponseDto;
import com.mendohard.api.exception.DatosNoValidosException;
import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.*;
import com.mendohard.api.repository.ConsumidorRepository;
import com.mendohard.api.repository.EstadoComercioRepository;
import com.mendohard.api.repository.EstadoVendedorRepository;
import com.mendohard.api.repository.VendedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InhabilitarUsuarioServiceImpl implements InhabilitarUsuarioService {

    private final ConsumidorRepository consumidorRepository;
    private final VendedorRepository vendedorRepository;
    private final EstadoVendedorRepository estadoVendedorRepository;
    private final EstadoComercioRepository estadoComercioRepository;

    @Override
    public List<ConsumidorInhabilitarResponseDto> obtenerConsumidoresActivos() {
        return consumidorRepository.findByUFechaBajaIsNull().stream()
                .map(consumidor -> ConsumidorInhabilitarResponseDto.builder()
                        .uCodigo(consumidor.getUCodigo())
                        .uNombre(consumidor.getUNombre())
                        .uApellido(consumidor.getUApellido())
                        .uEmail(consumidor.getUEmail())
                        .cApodo(consumidor.getCApodo())
                        .uFechaAlta(consumidor.getUFechaAlta())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void inhabilitarConsumidor(String uCodigo) {
        Consumidor consumidor = consumidorRepository.findByUCodigoAndUFechaBajaIsNull(uCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Consumidor no encontrado o ya se encuentra inhabilitado"));

        consumidor.setUFechaBaja(LocalDate.now());
        consumidorRepository.save(consumidor);
    }

    @Override
    public List<VendedorInhabilitarResponseDto> obtenerVendedoresAceptadosActivos() {
        return vendedorRepository.findVendedoresAceptadosActivos().stream()
                .map(vendedor -> VendedorInhabilitarResponseDto.builder()
                        .uCodigo(vendedor.getUCodigo())
                        .uNombre(vendedor.getUNombre())
                        .uApellido(vendedor.getUApellido())
                        .uEmail(vendedor.getUEmail())
                        .vTelefono(vendedor.getVTelefono())
                        .vCuit(vendedor.getVCuit())
                        .vRazonSocial(vendedor.getVRazonSocial())
                        .vCategoriaFiscal(vendedor.getVCategoriaFiscal())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void inhabilitarVendedorYComercios(String uCodigo) {
        Vendedor vendedor = vendedorRepository.findByUCodigoAndUFechaBajaIsNull(uCodigo)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado o ya se encuentra inhabilitado"));

        // 1. Actualizar estado del Vendedor
        VendedorEstado estadoVigente = vendedor.getVendedorEstados().stream()
                .filter(ve -> ve.getVEFechaHasta() == null)
                .findFirst()
                .orElseThrow(() -> new DatosNoValidosException("El vendedor no posee un estado vigente"));

        estadoVigente.setVEFechaHasta(LocalDate.now());

        EstadoVendedor estadoInhabilitado = estadoVendedorRepository.findByNombreActivo("VendedorInhabilitado")
                .orElseThrow(() -> new DatosNoValidosException("El estado 'VendedorInhabilitado' no se encuentra configurado en el sistema"));

        VendedorEstado nuevoEstado = VendedorEstado.builder()
                .VEFechaDesde(LocalDate.now())
                .VEFechaHasta(null)
                .estadoVendedor(estadoInhabilitado)
                .build();
        
        vendedor.getVendedorEstados().add(nuevoEstado);

        // 2. Inhabilitar Vendedor
        vendedor.setUFechaBaja(LocalDate.now());

        // 3. Procesar Comercios asociados
        if (vendedor.getComercios() != null) {
            EstadoComercio comercioInhabilitado = null;
            for (Comercio comercio : vendedor.getComercios()) {
                if (comercio.getCFechaBaja() == null) {
                    ComercioEstado comercioEstadoVigente = comercio.getComercioEstados().stream()
                            .filter(ce -> ce.getCEFechaHasta() == null && "ComercioAceptado".equals(ce.getEstadoComercio().getECNombre()))
                            .findFirst()
                            .orElse(null);

                    if (comercioEstadoVigente != null) {
                        comercioEstadoVigente.setCEFechaHasta(LocalDate.now());

                        if (comercioInhabilitado == null) {
                            comercioInhabilitado = estadoComercioRepository.findByNombreActivo("ComercioInhabilitado")
                                    .orElseThrow(() -> new DatosNoValidosException("El estado 'ComercioInhabilitado' no se encuentra configurado en el sistema"));
                        }

                        ComercioEstado nuevoComercioEstado = ComercioEstado.builder()
                                .CEFechaDesde(LocalDate.now())
                                .CEFechaHasta(null)
                                .estadoComercio(comercioInhabilitado)
                                .build();

                        comercio.getComercioEstados().add(nuevoComercioEstado);
                        comercio.setCFechaBaja(LocalDate.now());
                    }
                }
            }
        }

        vendedorRepository.save(vendedor);
    }
}
