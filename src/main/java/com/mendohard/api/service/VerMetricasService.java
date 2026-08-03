package com.mendohard.api.service;

import com.mendohard.api.dto.MetricasRequestDTO;
import com.mendohard.api.dto.MetricasResponseDTO;

public interface VerMetricasService {
    MetricasResponseDTO obtenerMetricas(MetricasRequestDTO request);
}
