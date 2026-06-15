package com.mendohard.api.service.strategy;

import com.mendohard.api.model.Usuario;

public interface ClaveStrategy {
    void generarYGuardarClave(Usuario usuario, String contrasenaRaw);
    boolean verificarContrasena(String contrasenaRaw, String contrasenaHasheada, String salt);
}
