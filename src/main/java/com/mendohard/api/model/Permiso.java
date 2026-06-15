package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "permiso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "p_codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "p_nombre", nullable = false)
    private String nombre;

    @Column(name = "p_descripcion")
    private String descripcion;

    @Column(name = "p_fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "p_fecha_baja")
    private LocalDate fechaBaja;
}
