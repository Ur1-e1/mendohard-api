package com.mendohard.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MetricasRequestDTO(
    @NotNull(message = "La fecha desde es requerida")
    @PastOrPresent(message = "La fecha desde no puede ser una fecha futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fechaDesde,

    @NotNull(message = "La fecha hasta es requerida")
    @PastOrPresent(message = "La fecha hasta no puede ser una fecha futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate fechaHasta
) {}
