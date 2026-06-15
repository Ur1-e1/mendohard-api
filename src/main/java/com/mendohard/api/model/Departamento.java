package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "departamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "d_codigo", nullable = false, unique = true)
    private String DCodigo;

    @Column(name = "d_nombre", nullable = false)
    private String DNombre;

    @Column(name = "d_fecha_alta")
    private LocalDate DFechaAlta;

    @Column(name = "d_fecha_baja")
    private LocalDate DFechaBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id", nullable = false)
    private Provincia provincia;
}
