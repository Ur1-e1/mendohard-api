package com.mendohard.api.service;

import com.mendohard.api.dto.ComercioVendedorResponseDTO;
import com.mendohard.api.dto.ConfirmarStockRequestDTO;
import com.mendohard.api.dto.ConsultaStockPendienteResponseDTO;
import com.mendohard.api.dto.ConsultaStockRespuestaDTO;
import com.mendohard.api.dto.NivelStockResponseDTO;

import java.util.List;

public interface ConfirmarStockService {
    
    List<ComercioVendedorResponseDTO> listarComerciosVendedor();

    List<ConsultaStockPendienteResponseDTO> listarConsultasPendientes(String cCodigo);

    List<NivelStockResponseDTO> obtenerNivelesStockJerarquicos(Long csContador);

    ConsultaStockRespuestaDTO confirmarStock(Long csContador, ConfirmarStockRequestDTO requestDTO);
}
