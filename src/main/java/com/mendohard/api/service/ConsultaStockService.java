package com.mendohard.api.service;

import com.mendohard.api.dto.ComercioResumenDTO;
import com.mendohard.api.dto.ConsultaCincoCercanosRequestDTO;
import com.mendohard.api.dto.ConsultaSucursalEspecificaRequestDTO;
import com.mendohard.api.dto.MapaStockComercioResponseDTO;

import java.util.List;

public interface ConsultaStockService {
    List<ComercioResumenDTO> obtenerComerciosActivosParaUI45();

    List<MapaStockComercioResponseDTO> consultarStockSucursalEspecifica(ConsultaSucursalEspecificaRequestDTO dto, String usuarioEmail);

    List<MapaStockComercioResponseDTO> consultarStockCincoCercanos(ConsultaCincoCercanosRequestDTO dto, String usuarioEmail);

    List<MapaStockComercioResponseDTO> obtenerMapaStockDisponibilidad(String pCodigo);
}
