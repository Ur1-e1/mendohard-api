package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "rol_permiso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolPermiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rp_fecha_desde", nullable = false)
    private LocalDate RPFechaDesde;

    @Column(name = "rp_fecha_hasta")
    private LocalDate RPFechaHasta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permiso_id", nullable = false)
    private Permiso permiso;
}
