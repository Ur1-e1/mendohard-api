package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "comercio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Comercio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_codigo", nullable = false, unique = true)
    private String CCodigo;

    @Column(name = "c_nombre_fantasia", nullable = false)
    private String CNombreFantasia;

    @Column(name = "c_telefono")
    private String CTelefono;

    @Column(name = "c_latitud")
    private Float CLatitud;

    @Column(name = "c_longitud")
    private Float CLongitud;

    @Column(name = "c_direccion_calle")
    private String CDireccionCalle;

    @Column(name = "c_numero_en_calle")
    private String CNumeroEnCalle;

    @Column(name = "c_fecha_solicitud")
    private LocalDate CFechaSolicitud;

    @Column(name = "c_fecha_alta")
    private LocalDate CFechaAlta;

    @Column(name = "c_fecha_baja")
    private LocalDate CFechaBaja;

    @Column(name = "c_horario_atencion")
    private String CHorarioAtencion;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "comercio_id")
    private List<ComercioEstado> comercioEstados;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;
}
