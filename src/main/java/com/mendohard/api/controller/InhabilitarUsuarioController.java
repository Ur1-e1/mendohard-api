package com.mendohard.api.controller;

import com.mendohard.api.dto.ConsumidorInhabilitarResponseDto;
import com.mendohard.api.dto.VendedorInhabilitarResponseDto;
import com.mendohard.api.service.InhabilitarUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios/inhabilitar")
@RequiredArgsConstructor
public class InhabilitarUsuarioController {

    private final InhabilitarUsuarioService inhabilitarUsuarioService;

    @GetMapping("/consumidores")
    public ResponseEntity<List<ConsumidorInhabilitarResponseDto>> obtenerConsumidoresActivos() {
        return ResponseEntity.ok(inhabilitarUsuarioService.obtenerConsumidoresActivos());
    }

    @PutMapping("/consumidores/{uCodigo}")
    public ResponseEntity<Void> inhabilitarConsumidor(@PathVariable("uCodigo") String uCodigo) {
        inhabilitarUsuarioService.inhabilitarConsumidor(uCodigo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/vendedores")
    public ResponseEntity<List<VendedorInhabilitarResponseDto>> obtenerVendedoresAceptadosActivos() {
        return ResponseEntity.ok(inhabilitarUsuarioService.obtenerVendedoresAceptadosActivos());
    }

    @PutMapping("/vendedores/{uCodigo}")
    public ResponseEntity<Void> inhabilitarVendedor(@PathVariable("uCodigo") String uCodigo) {
        inhabilitarUsuarioService.inhabilitarVendedorYComercios(uCodigo);
        return ResponseEntity.noContent().build();
    }
}
