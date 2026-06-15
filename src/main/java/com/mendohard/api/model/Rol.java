package com.mendohard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "r_codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "r_nombre", nullable = false)
    private String nombre;

    @Column(name = "r_descripcion")
    private String descripcion;

    @Column(name = "r_fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "r_fecha_baja")
    private LocalDate fechaBaja;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id")
    private List<RolPermiso> rolPermisos;
}
