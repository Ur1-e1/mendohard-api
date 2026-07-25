package com.mendohard.api.service.strategy;

import com.mendohard.api.model.Usuario;

public interface ClaveStrategy {
    void generarYGuardarClave(Usuario usuario, String contrasenaRaw);
    boolean verificarContrasena(String contrasenaRaw, String contrasenaHasheada, String salt);

    /**
     * CU-04 — Camino Alternativo N°2/N°6: Modifica la clave existente de un usuario.
     * Recupera la entidad {@link com.mendohard.api.model.Clave} asociada, genera un nuevo
     * salt numérico, recalcula el hash criptográfico y persiste los cambios.
     *
     * @param usuario          Usuario cuya clave se va a actualizar (debe existir en BD)
     * @param nuevaContrasenaRaw Contraseña en texto plano a hashear
     */
    void modificarYGuardarClave(Usuario usuario, String nuevaContrasenaRaw);
}

