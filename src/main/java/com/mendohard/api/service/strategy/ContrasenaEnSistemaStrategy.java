package com.mendohard.api.service.strategy;

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

    private String generarSalt() {
        Random random = new Random();
        int saltNumerico = 100000 + random.nextInt(900000);
        return String.valueOf(saltNumerico);
    }
}
