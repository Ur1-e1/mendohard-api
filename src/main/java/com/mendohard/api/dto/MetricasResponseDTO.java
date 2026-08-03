package com.mendohard.api.dto;

import java.util.List;

public record MetricasResponseDTO(
    List<ComponenteDemandadoDTO> componentesMasDemandados,
    List<DemandaInsatisfechaDTO> demandaInsatisfechaPorZona
) {}
