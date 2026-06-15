package com.mendohard.api.service.strategy;

import com.mendohard.api.model.Usuario;

public interface ClaveStrategy {
    void generarYGuardarClave(Usuario usuario, String contrasenaRaw);
}
