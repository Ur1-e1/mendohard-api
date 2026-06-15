package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Entity
@Table(name = "estado_vendedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EstadoVendedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ev_codigo", nullable = false, unique = true)
    private String EVCodigo;

    @Column(name = "ev_nombre", nullable = false)
    private String EVNombre;

    @Column(name = "ev_fecha_baja")
    private LocalDate EVFechaBaja;
}
