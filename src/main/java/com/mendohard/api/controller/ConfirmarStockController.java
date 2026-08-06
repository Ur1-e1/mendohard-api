package com.mendohard.api.controller;

import com.mendohard.api.dto.ComercioVendedorResponseDTO;
import com.mendohard.api.dto.ConfirmarStockRequestDTO;
import com.mendohard.api.dto.ConsultaStockPendienteResponseDTO;
import com.mendohard.api.dto.ConsultaStockRespuestaDTO;
import com.mendohard.api.dto.NivelStockResponseDTO;
import com.mendohard.api.service.ConfirmarStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConfirmarStockController {

    private final ConfirmarStockService confirmarStockService;

    @GetMapping("/vendedores/me/comercios")
    public ResponseEntity<List<ComercioVendedorResponseDTO>> listarComerciosVendedor() {
        return ResponseEntity.ok(confirmarStockService.listarComerciosVendedor());
    }

    @GetMapping("/comercios/{cCodigo}/consultas-stock/pendientes")
    public ResponseEntity<List<ConsultaStockPendienteResponseDTO>> listarConsultasPendientes(@PathVariable("cCodigo") String cCodigo) {
        return ResponseEntity.ok(confirmarStockService.listarConsultasPendientes(cCodigo));
    }

    @GetMapping("/consultas-stock/{csContador}/niveles-stock")
    public ResponseEntity<List<NivelStockResponseDTO>> obtenerNivelesStock(@PathVariable("csContador") Long csContador) {
        return ResponseEntity.ok(confirmarStockService.obtenerNivelesStockJerarquicos(csContador));
    }

    @PostMapping("/consultas-stock/{csContador}/confirmar")
    public ResponseEntity<ConsultaStockRespuestaDTO> confirmarStock(
            @PathVariable("csContador") Long csContador,
            @Valid @RequestBody ConfirmarStockRequestDTO requestDTO) {
        return ResponseEntity.ok(confirmarStockService.confirmarStock(csContador, requestDTO));
    }
}
