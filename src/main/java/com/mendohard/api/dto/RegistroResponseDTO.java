package com.mendohard.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistroResponseDTO {

    private Long usuarioId;
    private String UCodigo;
    private String UEmail;
    private String UNombre;
    private String tipoUsuario;
}
