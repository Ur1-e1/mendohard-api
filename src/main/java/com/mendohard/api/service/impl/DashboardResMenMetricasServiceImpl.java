package com.mendohard.api.service.impl;

import com.mendohard.api.dto.DashboardResMenMetricasResponseDTO;
import com.mendohard.api.repository.ComercioEstadoRepository;
import com.mendohard.api.repository.ConsumidorRepository;
import com.mendohard.api.repository.VendedorEstadoRepository;
import com.mendohard.api.repository.VendedorRepository;
import com.mendohard.api.service.DashboardResMenMetricasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardResMenMetricasServiceImpl implements DashboardResMenMetricasService {

    private final ConsumidorRepository consumidorRepository;
    private final VendedorRepository vendedorRepository;
    private final VendedorEstadoRepository vendedorEstadoRepository;
    private final ComercioEstadoRepository comercioEstadoRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardResMenMetricasResponseDTO obtenerMetricas() {
        log.info("Calculando métricas para el dashboard del Responsable MendoHard");
        
        Long usuariosTotales = consumidorRepository.countConsumidoresActivos() + vendedorRepository.countVendedoresAceptadosActivos();
        Long vendedoresPendientes = vendedorEstadoRepository.countVendedoresPendientes();
        Long comerciosActivos = comercioEstadoRepository.countComerciosActivos();

        return DashboardResMenMetricasResponseDTO.builder()
                .usuariosTotales(usuariosTotales)
                .vendedoresPendientes(vendedoresPendientes)
                .comerciosActivos(comerciosActivos)
                .build();
    }
}
