package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "vendedor_estado")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VendedorEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ve_fecha_desde")
    private LocalDate VEFechaDesde;

    @Column(name = "ve_fecha_hasta")
    private LocalDate VEFechaHasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_vendedor_id", nullable = false)
    private EstadoVendedor estadoVendedor;
}
