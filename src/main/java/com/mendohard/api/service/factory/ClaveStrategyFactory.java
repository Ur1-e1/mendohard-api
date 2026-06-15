package com.mendohard.api.service.factory;

import com.mendohard.api.exception.RegistroException;
import com.mendohard.api.service.strategy.ClaveStrategy;
import com.mendohard.api.service.strategy.ContrasenaEnSistemaStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClaveStrategyFactory {

    private final ContrasenaEnSistemaStrategy contrasenaEnSistemaStrategy;

    public ClaveStrategy getStrategy(String nombreAlgoritmo) {
        if (nombreAlgoritmo == null) {
            throw new RegistroException("El nombre del algoritmo de clave no puede ser nulo");
        }

        // Hacemos match exacto con el nombre inyectado en el script del ApiApplication ("ContraseñaEnSistema")
        return switch (nombreAlgoritmo) {
            case "ContraseñaEnSistema" -> contrasenaEnSistemaStrategy;
            default -> throw new RegistroException("Estrategia criptográfica no soportada: " + nombreAlgoritmo);
        };
    }
}
