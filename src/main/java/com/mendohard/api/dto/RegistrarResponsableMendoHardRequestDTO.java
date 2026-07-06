package com.mendohard.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistrarResponsableMendoHardRequestDTO(
        @NotBlank(message = "El nombre no puede estar vacío")
        String uNombre,

        @NotBlank(message = "El apellido no puede estar vacío")
        String uApellido,

        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "El formato del email es inválido")
        String uEmail,

        @NotNull(message = "El legajo no puede ser nulo")
        String rmhLegajo,

        @NotBlank(message = "La contraseña no puede estar vacía")
        String contrasenna,

        @NotBlank(message = "La confirmación de la contraseña no puede estar vacía")
        String confirmacionContrasenna
) {}
