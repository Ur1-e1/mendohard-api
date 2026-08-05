package com.mendohard.api.controller;

import com.mendohard.api.dto.RegistrarResponsableMendoHardRequestDTO;
import com.mendohard.api.dto.RegistrarResponsableMendoHardResponseDTO;
import com.mendohard.api.service.ResponsableMendoHardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/responsables")
public class ResponsableMendoHardController {

    private final ResponsableMendoHardService responsableMendoHardService;

    public ResponsableMendoHardController(ResponsableMendoHardService responsableMendoHardService) {
        this.responsableMendoHardService = responsableMendoHardService;
    }

    @PostMapping("/registro")
    public ResponseEntity<RegistrarResponsableMendoHardResponseDTO> registrarResponsable(
            @Valid @RequestBody RegistrarResponsableMendoHardRequestDTO request,
            @RequestHeader("Authorization") String token // O el mecanismo que uses para obtener la sesión
    ) {
        // Enviaremos el identificador del usuario en sesión al servicio para validar la
        // precondición
        RegistrarResponsableMendoHardResponseDTO response = responsableMendoHardService.registrarResponsable(request,
                token);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
