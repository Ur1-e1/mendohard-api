package com.mendohard.api.controller;

import com.mendohard.api.dto.DashboardResMenMetricasResponseDTO;
import com.mendohard.api.service.DashboardResMenMetricasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard/responsable")
@RequiredArgsConstructor
@Slf4j
public class DashboardResMenMetricasController {

    private final DashboardResMenMetricasService metricsService;

    @GetMapping("/metricas")
    public ResponseEntity<DashboardResMenMetricasResponseDTO> obtenerMetricas() {
        log.info("GET /api/v1/dashboard/responsable/metricas — Solicitud recibida");
        DashboardResMenMetricasResponseDTO response = metricsService.obtenerMetricas();
        return ResponseEntity.ok(response);
    }
}
