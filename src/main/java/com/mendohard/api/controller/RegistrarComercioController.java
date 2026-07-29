package com.mendohard.api.controller;

import com.mendohard.api.dto.ComercioResponseDTO;
import com.mendohard.api.dto.RegistrarComercioRequestDTO;
import com.mendohard.api.dto.UbicacionCascadaDTO;
import com.mendohard.api.service.RegistrarComercioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comercios")
@RequiredArgsConstructor
public class RegistrarComercioController {

    private final RegistrarComercioService registrarComercioService;

    @GetMapping("/ubicaciones")
    public ResponseEntity<List<UbicacionCascadaDTO>> obtenerUbicacionesActivas() {
        List<UbicacionCascadaDTO> response = registrarComercioService.obtenerUbicacionesActivas();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ComercioResponseDTO> registrarComercio(@Valid @RequestBody RegistrarComercioRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailUsuario = authentication.getName();

        ComercioResponseDTO response = registrarComercioService.registrarComercio(request, emailUsuario);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
