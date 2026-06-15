package com.mendohard.api.controller;


import com.mendohard.api.dto.RegistroConsumidorRequestDTO;
import com.mendohard.api.dto.RegistroResponseDTO;
import com.mendohard.api.dto.RegistroVendedorRequestDTO;
import com.mendohard.api.dto.UbicacionesVendedorDTO;
import com.mendohard.api.service.RegistroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/registro")
@RequiredArgsConstructor
@Slf4j
public class RegistroController {

    private final RegistroService registroService;

    @GetMapping("/vendedor/ubicaciones")
    public ResponseEntity<List<UbicacionesVendedorDTO>> obtenerUbicacionesVendedor() {
        log.info("GET /api/v1/auth/registro/vendedor/ubicaciones");
        List<UbicacionesVendedorDTO> ubicaciones = registroService.obtenerUbicacionesVendedor();
        return ResponseEntity.status(HttpStatus.OK).body(ubicaciones);
    }

    @PostMapping("/consumidor")
    public ResponseEntity<RegistroResponseDTO> registrarConsumidor(
            @Valid @RequestBody RegistroConsumidorRequestDTO request) {
        log.info("POST /api/v1/auth/registro/consumidor - Email: {}", request.getUEmail());
        RegistroResponseDTO response = registroService.registrarConsumidor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/vendedor")
    public ResponseEntity<RegistroResponseDTO> registrarVendedor(
            @Valid @RequestBody RegistroVendedorRequestDTO request) {
        log.info("POST /api/v1/auth/registro/vendedor - Email: {}", request.getUEmail());
        RegistroResponseDTO response = registroService.registrarVendedor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}