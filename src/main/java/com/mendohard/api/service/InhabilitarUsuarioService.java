package com.mendohard.api.service;

import com.mendohard.api.dto.ConsumidorInhabilitarResponseDto;
import com.mendohard.api.dto.VendedorInhabilitarResponseDto;

import java.util.List;

public interface InhabilitarUsuarioService {

    List<ConsumidorInhabilitarResponseDto> obtenerConsumidoresActivos();

    void inhabilitarConsumidor(String uCodigo);

    List<VendedorInhabilitarResponseDto> obtenerVendedoresAceptadosActivos();

    void inhabilitarVendedorYComercios(String uCodigo);
}
