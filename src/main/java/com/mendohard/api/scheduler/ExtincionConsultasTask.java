package com.mendohard.api.scheduler;

import com.mendohard.api.service.ExtincionConsultasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExtincionConsultasTask {

    private final ExtincionConsultasService extincionConsultasService;

    @Scheduled(fixedDelay = 900000)
    public void ejecutarExtincion() {
        try {
            extincionConsultasService.procesarExtincionConsultas();
        } catch (Exception e) {
            log.error("[RELOJ] Ocurrió un error en el proceso de extinción: {}", e.getMessage());
        }
    }
}
