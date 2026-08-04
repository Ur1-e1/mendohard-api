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
public class IniciarSesionResponseDTO {

    private String email;

    private String rolNombre;

    private String nombreCompleto;

    private String redireccionHome;

    private String token;

    private ExtraDataResponsableDTO extraData;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExtraDataResponsableDTO {
        private Long vendedoresPendientes;
        private Long usuariosTotales;
        private Long comerciosActivos;
    }
}
