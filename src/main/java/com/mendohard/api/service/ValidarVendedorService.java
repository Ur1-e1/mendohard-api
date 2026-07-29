package com.mendohard.api.service;

import com.mendohard.api.dto.ValidarVendedorRequestDTO;
import com.mendohard.api.dto.VendedorDetalleResponseDTO;
import com.mendohard.api.dto.VendedorPendienteResponseDTO;

import java.util.List;

public interface ValidarVendedorService {

    List<VendedorPendienteResponseDTO> obtenerVendedoresPendientes();

    VendedorDetalleResponseDTO obtenerDetalleVendedorPendiente(String uCodigo);

    void validarVendedor(ValidarVendedorRequestDTO request);
}
