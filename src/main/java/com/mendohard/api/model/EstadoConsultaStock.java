package com.mendohard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "estado_consulta_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoConsultaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ecs_codigo", nullable = false, unique = true)
    private String ECSCodigo;

    @Column(name = "ecs_nombre", nullable = false)
    private String ECSNombre;

    @Column(name = "ecs_fecha_baja")
    private LocalDate ECSFechaBaja;
}
