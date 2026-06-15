package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "algoritmo_clave")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlgoritmoClave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ac_codigo", nullable = false, unique = true)
    private String ACCodigo;

    @Column(name = "ac_nombre", nullable = false)
    private String ACNombre;

    @Column(name = "ac_fecha_baja")
    private LocalDate ACFechaBaja;
}
