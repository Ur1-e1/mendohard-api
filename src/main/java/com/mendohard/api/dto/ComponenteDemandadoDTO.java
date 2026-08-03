package com.mendohard.api.dto;

public record ComponenteDemandadoDTO(
    String nombre,
    Long cantidad,
    Object especificacion
) {}
