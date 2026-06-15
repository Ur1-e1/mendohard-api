package com.mendohard.api.service;


import com.mendohard.api.dto.IniciarSesionRequestDTO;
import com.mendohard.api.dto.IniciarSesionResponseDTO;

public interface IniciarSesionService {

    IniciarSesionResponseDTO procesarIngreso(IniciarSesionRequestDTO request);
}