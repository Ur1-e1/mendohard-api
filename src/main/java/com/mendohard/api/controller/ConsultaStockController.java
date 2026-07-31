package com.mendohard.api.controller;

import com.mendohard.api.dto.ComercioResumenDTO;
import com.mendohard.api.dto.ConsultaCincoCercanosRequestDTO;
import com.mendohard.api.dto.ConsultaSucursalEspecificaRequestDTO;
import com.mendohard.api.dto.MapaStockComercioResponseDTO;
import com.mendohard.api.service.ConsultaStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consultas-stock")
@RequiredArgsConstructor
public class ConsultaStockController {

    private final ConsultaStockService consultaStockService;

    @GetMapping("/comercios")
    public ResponseEntity<List<ComercioResumenDTO>> obtenerComerciosActivos() {
        return ResponseEntity.ok(consultaStockService.obtenerComerciosActivosParaUI45());
    }

    @PostMapping("/especifica")
    public ResponseEntity<List<MapaStockComercioResponseDTO>> consultarStockSucursalEspecifica(
            @Valid @RequestBody ConsultaSucursalEspecificaRequestDTO dto,
            Authentication authentication) {
        String usuarioEmail = authentication.getName();
        return ResponseEntity.ok(consultaStockService.consultarStockSucursalEspecifica(dto, usuarioEmail));
    }

    @PostMapping("/cercanos")
    public ResponseEntity<List<MapaStockComercioResponseDTO>> consultarStockCincoCercanos(
            @Valid @RequestBody ConsultaCincoCercanosRequestDTO dto,
            Authentication authentication) {
        String usuarioEmail = authentication.getName();
        return ResponseEntity.ok(consultaStockService.consultarStockCincoCercanos(dto, usuarioEmail));
    }

    @GetMapping("/mapa")
    public ResponseEntity<List<MapaStockComercioResponseDTO>> obtenerMapaStockDisponibilidad(
            @RequestParam String pCodigo) {
        return ResponseEntity.ok(consultaStockService.obtenerMapaStockDisponibilidad(pCodigo));
    }
}
