package com.mendohard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CU-05 Paso 1 — DTO de entrada para solicitar el código OTP de recuperación.
 * Endpoint: POST /api/v1/auth/recuperar-credencial/solicitar
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitarRecuperacionDto {

    @NotBlank(message = "El email es obligatorio")
    @Pattern(regexp = "^\\S+$", message = "El email no debe contener espacios")
    @Email(message = "El email debe ser válido")
    private String email;
}
