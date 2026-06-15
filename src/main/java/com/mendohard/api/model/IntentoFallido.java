package com.mendohard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "intento_fallido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntentoFallido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "if_codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "if_cantidad", nullable = false)
    private int cantidad;

    @Column(name = "if_fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}