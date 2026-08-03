package com.mendohard.api.controller;

import com.mendohard.api.dto.MetricasRequestDTO;
import com.mendohard.api.dto.MetricasResponseDTO;
import com.mendohard.api.service.VerMetricasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/metricas")
@RequiredArgsConstructor
public class MetricasController {

    private final VerMetricasService verMetricasService;

    @GetMapping
    public ResponseEntity<MetricasResponseDTO> obtenerMetricas(@Valid MetricasRequestDTO request) {
        MetricasResponseDTO response = verMetricasService.obtenerMetricas(request);
        return ResponseEntity.ok(response);
    }
}
