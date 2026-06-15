package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "comercio_estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ComercioEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ce_fecha_desde")
    private LocalDate CEFechaDesde;

    @Column(name = "ce_fecha_hasta")
    private LocalDate CEFechaHasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_comercio_id", nullable = false)
    private EstadoComercio estadoComercio;
}
