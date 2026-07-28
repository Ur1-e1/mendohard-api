package com.mendohard.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumidorInhabilitarResponseDto {
    private String uCodigo;
    private String uNombre;
    private String uApellido;
    private String uEmail;
    private String cApodo;
    private LocalDate uFechaAlta;
}
