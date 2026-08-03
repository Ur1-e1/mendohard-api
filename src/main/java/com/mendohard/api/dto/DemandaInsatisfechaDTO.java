package com.mendohard.api.dto;

public record DemandaInsatisfechaDTO(
    String nombre,
    Long cantidad,
    String departamento,
    Object especificacion
) {}
