package com.mendohard.api.service.impl;

import com.mendohard.api.dto.ComponenteDemandadoDTO;
import com.mendohard.api.dto.DemandaInsatisfechaDTO;
import com.mendohard.api.dto.MetricasRequestDTO;
import com.mendohard.api.dto.MetricasResponseDTO;
import com.mendohard.api.exception.DatosNoValidosException;
import com.mendohard.api.repository.ConsultaStockRepository;
import com.mendohard.api.service.VerMetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerMetricasServiceImpl implements VerMetricasService {

    private final ConsultaStockRepository consultaStockRepository;

    @Override
    @Transactional(readOnly = true)
    public MetricasResponseDTO obtenerMetricas(MetricasRequestDTO request) {
        if (request.fechaDesde().isAfter(request.fechaHasta())) {
            throw new DatosNoValidosException("Datos ingresados no válidos");
        }

        LocalDateTime fechaInicio = request.fechaDesde().atStartOfDay();
        LocalDateTime fechaFin = request.fechaHasta().atTime(23, 59, 59, 999999999);

        List<ComponenteDemandadoDTO> componentes = consultaStockRepository
                .findComponentesMasDemandados(fechaInicio, fechaFin);
        
        List<DemandaInsatisfechaDTO> demandas = consultaStockRepository
                .findDemandaInsatisfechaPorZona(fechaInicio, fechaFin);

        return new MetricasResponseDTO(componentes, demandas);
    }
}
