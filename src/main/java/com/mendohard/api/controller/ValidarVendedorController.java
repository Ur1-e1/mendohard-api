package com.mendohard.api.controller;

import com.mendohard.api.dto.ValidarVendedorRequestDTO;
import com.mendohard.api.dto.VendedorDetalleResponseDTO;
import com.mendohard.api.dto.VendedorPendienteResponseDTO;
import com.mendohard.api.service.ValidarVendedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendedores")
@RequiredArgsConstructor
public class ValidarVendedorController {

    private final ValidarVendedorService validarVendedorService;

    @GetMapping("/pendientes")
    public ResponseEntity<List<VendedorPendienteResponseDTO>> obtenerVendedoresPendientes() {
        List<VendedorPendienteResponseDTO> response = validarVendedorService.obtenerVendedoresPendientes();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendientes/{uCodigo}")
    public ResponseEntity<VendedorDetalleResponseDTO> obtenerDetalleVendedorPendiente(@PathVariable String uCodigo) {
        VendedorDetalleResponseDTO response = validarVendedorService.obtenerDetalleVendedorPendiente(uCodigo);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/validar")
    public ResponseEntity<Void> validarVendedor(@Valid @RequestBody ValidarVendedorRequestDTO request) {
        validarVendedorService.validarVendedor(request);
        return ResponseEntity.ok().build();
    }
}
