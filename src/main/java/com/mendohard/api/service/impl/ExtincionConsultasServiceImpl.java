package com.mendohard.api.service.impl;

import com.mendohard.api.exception.ResourceNotFoundException;
import com.mendohard.api.model.EstadoConsultaStock;
import com.mendohard.api.repository.ConsultaStockRepository;
import com.mendohard.api.repository.EstadoConsultaStockRepository;
import com.mendohard.api.service.ExtincionConsultasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExtincionConsultasServiceImpl implements ExtincionConsultasService {

    private final EstadoConsultaStockRepository estadoConsultaStockRepository;
    private final ConsultaStockRepository consultaStockRepository;

    @Override
    @Transactional
    public void procesarExtincionConsultas() {
        log.info("[RELOJ] Iniciando proceso de extinción de consultas de stock...");

        List<String> nombresOrigen = List.of("StockPendiente", "SinStock", "StockDisponible");
        List<EstadoConsultaStock> estadosOrigen = estadoConsultaStockRepository.findByECSNombreInAndECSFechaBajaIsNull(nombresOrigen);

        if (estadosOrigen.isEmpty()) {
            log.warn("[RELOJ] No se encontraron estados de origen activos para la extinción de consultas.");
            throw new ResourceNotFoundException("No se encontraron estados de origen válidos.");
        }

        EstadoConsultaStock estadoDestino = estadoConsultaStockRepository.findByECSNombreAndECSFechaBajaIsNull("ConsultaExpirada")
                .orElseThrow(() -> {
                    log.warn("[RELOJ] No se encontró el estado destino 'ConsultaExpirada'.");
                    return new ResourceNotFoundException("El estado destino 'ConsultaExpirada' no existe.");
                });

        int consultasAfectadas = consultaStockRepository.extinguirConsultasVencidas(
                estadoDestino,
                estadosOrigen,
                LocalDateTime.now()
        );

        log.info("[RELOJ] Proceso de extinción completado. Consultas expiradas: {}", consultasAfectadas);
    }
}
