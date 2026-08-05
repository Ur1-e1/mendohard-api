package com.mendohard.api.controller;

import com.mendohard.api.dto.IniciarSesionRequestDTO;
import com.mendohard.api.dto.IniciarSesionResponseDTO;
import com.mendohard.api.service.IniciarSesionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class IniciarSesionController {

    private final IniciarSesionService iniciarSesionService;

    @PostMapping("/iniciar-sesion")
    public ResponseEntity<IniciarSesionResponseDTO> iniciarSesion(
            @Valid @RequestBody IniciarSesionRequestDTO request) {

        log.info("POST /api/auth/iniciar-sesion - Email: {}", request.getEmail());

        IniciarSesionResponseDTO response = iniciarSesionService.procesarIngreso(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
