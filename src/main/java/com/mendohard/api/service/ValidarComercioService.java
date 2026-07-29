package com.mendohard.api.service;

import com.mendohard.api.dto.ComercioDetalleValidacionDto;
import com.mendohard.api.dto.ComercioPendienteResponseDto;
import com.mendohard.api.dto.DecisionValidacionRequestDto;

import java.util.List;

public interface ValidarComercioService {

    List<ComercioPendienteResponseDto> consultarComerciosPendientes();

    ComercioDetalleValidacionDto obtenerComercioParaValidacion(String cCodigo);

    void procesarDecisionValidacion(String cCodigo, DecisionValidacionRequestDto requestDto);
}
