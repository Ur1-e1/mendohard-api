package com.mendohard.api.service.strategy;

import com.mendohard.api.exception.RegistroException;
import com.mendohard.api.model.Clave;
import com.mendohard.api.model.Usuario;
import com.mendohard.api.repository.ClaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContrasenaEnSistemaStrategy implements ClaveStrategy {

    private final ClaveRepository claveRepository;

    @Override
    public void generarYGuardarClave(Usuario usuario, String contrasenaRaw) {
        String salt = generarSalt();
        String textoAHasher = contrasenaRaw + salt;
        String contrasenaCifrada = DigestUtils.md5DigestAsHex(textoAHasher.getBytes());

        Clave clave = Clave.builder()
                .CCodigo(usuario.getUCodigo())
                .CContrasena(contrasenaCifrada)
                .CSalt(salt)
                .usuario(usuario)
                .build();

        claveRepository.save(clave);
        log.info("Clave guardada exitosamente usando estrategia 'ContraseñaEnSistema' (MD5) para usuario ID: {}", usuario.getId());
    }

    /**
     * CU-04 — CA N°2/N°6: Modifica la clave existente del usuario.
     * Recupera la entidad Clave, genera un nuevo salt numérico, recalcula el hash MD5
     * y persiste los nuevos valores sobre la instancia ya existente.
     */
    @Override
    public void modificarYGuardarClave(Usuario usuario, String nuevaContrasenaRaw) {
        Clave claveExistente = claveRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RegistroException(
                        "No se encontró la clave para el usuario con ID: " + usuario.getId()));

        String nuevoSalt = generarSalt();
        String textoAHasher = nuevaContrasenaRaw + nuevoSalt;
        String nuevaContrasenaCifrada = DigestUtils.md5DigestAsHex(textoAHasher.getBytes());

        claveExistente.setCSalt(nuevoSalt);
        claveExistente.setCContrasena(nuevaContrasenaCifrada);

        claveRepository.save(claveExistente);
        log.info("Clave actualizada exitosamente (MD5) para usuario ID: {}", usuario.getId());
    }

    private String generarSalt() {
        Random random = new Random();
        int saltNumerico = 100000 + random.nextInt(900000);
        return String.valueOf(saltNumerico);
    }

    @Override
    public boolean verificarContrasena(String contrasenaRaw, String contrasenaHasheada, String salt) {
        String textoAHasher = contrasenaRaw + salt;
        String hashIngresado = DigestUtils.md5DigestAsHex(textoAHasher.getBytes());
        return contrasenaHasheada.equals(hashIngresado);
    }

}
