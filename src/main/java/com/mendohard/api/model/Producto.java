package com.mendohard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "producto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "p_codigo", nullable = false, unique = true)
    private String PCodigo;

    @Column(name = "p_nombre_tecnico", nullable = false)
    private String PNombreTecnico;

    // Atributo con formato JSON (clave-valor para las especificaciones)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "p_especificaciones", columnDefinition = "jsonb")
    private Map<String, Object> PEspecificaciones;

    @Column(name = "p_imagen_url")
    private String PImagenUrl;

    @Column(name = "p_fecha_alta", nullable = false)
    private LocalDate PFechaAlta;

    @Column(name = "p_fecha_baja")
    private LocalDate PFechaBaja;

    // Relación N a 1 hacia Categoria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}
