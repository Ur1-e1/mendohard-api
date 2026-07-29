package com.mendohard.api.service;

import com.mendohard.api.dto.ComercioResponseDTO;
import com.mendohard.api.dto.RegistrarComercioRequestDTO;
import com.mendohard.api.dto.UbicacionCascadaDTO;

import java.util.List;

public interface RegistrarComercioService {
    List<UbicacionCascadaDTO> obtenerUbicacionesActivas();
    ComercioResponseDTO registrarComercio(RegistrarComercioRequestDTO request, String emailUsuario);
}
