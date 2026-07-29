package com.mendohard.api.controller;

import com.mendohard.api.dto.ComercioDetalleValidacionDto;
import com.mendohard.api.dto.ComercioPendienteResponseDto;
import com.mendohard.api.dto.DecisionValidacionRequestDto;
import com.mendohard.api.service.ValidarComercioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comercios/validar")
@RequiredArgsConstructor
public class ValidarComercioController {

    private final ValidarComercioService validarComercioService;

    @GetMapping("/pendientes")
    public ResponseEntity<List<ComercioPendienteResponseDto>> consultarComerciosPendientes() {
        return ResponseEntity.ok(validarComercioService.consultarComerciosPendientes());
    }

    @GetMapping("/{cCodigo}")
    public ResponseEntity<ComercioDetalleValidacionDto> obtenerComercioParaValidacion(@PathVariable String cCodigo) {
        return ResponseEntity.ok(validarComercioService.obtenerComercioParaValidacion(cCodigo));
    }

    @PostMapping("/{cCodigo}/decision")
    public ResponseEntity<String> procesarDecisionValidacion(
            @PathVariable String cCodigo,
            @Valid @RequestBody DecisionValidacionRequestDto requestDto) {
        
        validarComercioService.procesarDecisionValidacion(cCodigo, requestDto);
        return ResponseEntity.ok("Decisión de validación procesada con éxito");
    }
}
