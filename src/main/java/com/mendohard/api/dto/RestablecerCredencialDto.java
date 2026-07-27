package com.mendohard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CU-05 Paso 2 — DTO de entrada para restablecer la contraseña con el código OTP.
 * Endpoint: POST /api/v1/auth/recuperar-credencial/restablecer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestablecerCredencialDto {

    @NotBlank(message = "El email es obligatorio")
    @Pattern(regexp = "^\\S+$", message = "El email no debe contener espacios")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "El código de verificación es obligatorio")
    private String codigoIngresado;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaContrasena;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String confirmacionContrasena;
}
