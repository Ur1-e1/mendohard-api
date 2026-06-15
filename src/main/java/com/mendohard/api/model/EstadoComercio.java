package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "estado_comercio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EstadoComercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ec_codigo", nullable = false, unique = true)
    private String ECCodigo;

    @Column(name = "ec_nombre", nullable = false)
    private String ECNombre;

    @Column(name = "ec_fecha_baja")
    private LocalDate ECFechaBaja;
}
