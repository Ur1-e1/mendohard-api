package com.mendohard.api.service;


import com.mendohard.api.dto.RegistrarResponsableMendoHardRequestDTO;
import com.mendohard.api.dto.RegistrarResponsableMendoHardResponseDTO;

public interface ResponsableMendoHardService {

    RegistrarResponsableMendoHardResponseDTO registrarResponsable(
            RegistrarResponsableMendoHardRequestDTO request,
            String token
    );
}