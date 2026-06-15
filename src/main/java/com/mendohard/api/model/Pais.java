package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "pais")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "p_codigo", nullable = false, unique = true)
    private String PCodigo;

    @Column(name = "p_nombre", nullable = false)
    private String PNombre;

    @Column(name = "p_fecha_alta")
    private LocalDate PFechaAlta;

    @Column(name = "p_fecha_baja")
    private LocalDate PFechaBaja;
}
