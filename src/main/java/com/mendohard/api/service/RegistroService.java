package com.mendohard.api.service;

import com.mendohard.api.dto.RegistroConsumidorRequestDTO;
import com.mendohard.api.dto.RegistroResponseDTO;
import com.mendohard.api.dto.RegistroVendedorRequestDTO;
import com.mendohard.api.dto.UbicacionesVendedorDTO;

import java.util.List;

public interface RegistroService {

    List<UbicacionesVendedorDTO> obtenerUbicacionesVendedor();

    RegistroResponseDTO registrarConsumidor(RegistroConsumidorRequestDTO request);

    RegistroResponseDTO registrarVendedor(RegistroVendedorRequestDTO request);
}