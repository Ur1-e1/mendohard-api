package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "provincia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Provincia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pro_codigo", nullable = false, unique = true)
    private String ProCodigo;

    @Column(name = "pro_nombre", nullable = false)
    private String ProNombre;

    @Column(name = "pro_fecha_alta")
    private LocalDate ProFechaAlta;

    @Column(name = "pro_fecha_baja")
    private LocalDate ProFechaBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pais_id", nullable = false)
    private Pais pais;
}
