package com.mendohard.api.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroVendedorRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String UNombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String UApellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String UEmail;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String Contrasena;

    @NotBlank(message = "La confirmación de contraseña es obligatoria")
    private String ConfirmacionContrasena;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 10, max = 20, message = "El teléfono debe tener entre 10 y 20 caracteres")
    private String VTelefono;

    @NotBlank(message = "El CUIT es obligatorio")
    @Size(min = 11, max = 13, message = "El CUIT debe tener entre 11 y 13 caracteres")
    private String VCuit;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(min = 3, max = 100, message = "La razón social debe tener entre 3 y 100 caracteres")
    private String VRazonSocial;

    @NotBlank(message = "La categoría fiscal es obligatoria")
    private String VCategoriaFiscal;

    @NotBlank(message = "El nombre de fantasía del comercio es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre de fantasía debe tener entre 3 y 100 caracteres")
    private String CNombreFantasia;

    @NotBlank(message = "El teléfono del comercio es obligatorio")
    @Size(min = 10, max = 20, message = "El teléfono del comercio debe tener entre 10 y 20 caracteres")
    private String CTelefono;

    @NotBlank(message = "La dirección del comercio es obligatoria")
    @Size(min = 5, max = 100, message = "La dirección debe tener entre 5 y 100 caracteres")
    private String CDireccionCalle;

    @NotBlank(message = "El número en calle es obligatorio")
    @Size(min = 1, max = 10, message = "El número en calle debe tener entre 1 y 10 caracteres")
    private String CNumeroEnCalle;

    @NotNull(message = "La latitud es obligatoria")
    private Float CLatitud;

    @NotNull(message = "La longitud es obligatoria")
    private Float CLongitud;

    @NotBlank(message = "El horario de atención es obligatorio")
    private String CHorarioAtencion;

    @NotBlank(message = "El departamento es obligatorio (use el DCodigo)")
    private String DCodigo;
}