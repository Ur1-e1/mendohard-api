package com.mendohard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "nivel_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NivelStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ns_codigo", nullable = false, unique = true)
    private String NSCodigo;

    @Column(name = "ns_nombre", nullable = false)
    private String NSNombre;

    @Column(name = "ns_cantidad_desde", nullable = false)
    private Integer NSCantidadDesde;

    @Column(name = "ns_cantidad_hasta")
    private Integer NSCantidadHasta;

    @Column(name = "ns_fecha_alta", nullable = false)
    private LocalDate NSFechaAlta;

    @Column(name = "ns_fecha_baja")
    private LocalDate NSFechaBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
