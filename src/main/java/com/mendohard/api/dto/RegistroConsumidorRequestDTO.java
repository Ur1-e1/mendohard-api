package com.mendohard.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroConsumidorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String UNombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String UApellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String UEmail;

    @NotBlank(message = "El apodo es obligatorio")
    @Size(min = 3, max = 30, message = "El apodo debe tener entre 3 y 30 caracteres")
    private String CApodo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String Contrasena;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String ConfirmacionContrasena;
}