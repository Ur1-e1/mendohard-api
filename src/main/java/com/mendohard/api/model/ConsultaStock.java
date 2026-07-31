package com.mendohard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consulta_stock")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cs_contador", nullable = false)
    private Long CSContador;

    @Column(name = "cs_fecha_hora_solicitud", nullable = false)
    private LocalDateTime CSFechaHoraSolicitud;

    @Column(name = "cs_fecha_hora_respuesta")
    private LocalDateTime CSFechaHoraRespuesta;

    @Column(name = "cs_marcas_respuesta")
    private String CSMarcasRespuesta;

    @Column(name = "cs_descripcion_respuesta")
    private String CSDescripcionRespuesta;

    @Column(name = "cs_precio_respuesta")
    private Float CSPrecioRespuesta;

    @Column(name = "cs_latitud_demanda")
    private Float CSLatitudDemanda;

    @Column(name = "cs_longitud_demanda")
    private Float CSLongitudDemanda;

    @Column(name = "cs_fecha_hora_expiracion", nullable = false)
    private LocalDateTime CSFechaHoraExpiracion;

    @Column(name = "cs_cantidad_respuesta")
    private Integer CSCantidadRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comercio_id", nullable = false)
    private Comercio comercio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consumidor_id", nullable = false)
    private Consumidor consumidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_consulta_stock_id", nullable = false)
    private EstadoConsultaStock estadoConsultaStock;
}
