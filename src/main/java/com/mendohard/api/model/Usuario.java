package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "u_codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "u_nombre", nullable = false)
    private String nombre;

    @Column(name = "u_apellido", nullable = false)
    private String apellido;

    @Column(name = "u_email", nullable = false)
    private String email;

    @Column(name = "u_fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "u_fecha_baja")
    private LocalDate fechaBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algoritmo_clave_id", nullable = false)
    private AlgoritmoClave algoritmoClave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
}