package com.mendohard.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_codigo", nullable = false, unique = true)
    private String CCodigo;

    @Column(name = "c_nombre", nullable = false)
    private String CNombre;

    // Relación Reflexiva: Navegabilidad bidireccional para armar jerarquía (Padre / Hijas)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_padre_id")
    private Categoria categoriaPadre;

    @OneToMany(mappedBy = "categoriaPadre", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Categoria> subcategorias = new ArrayList<>();

    @Column(name = "c_fecha_alta", nullable = false)
    private LocalDate CFechaAlta;

    @Column(name = "c_fecha_baja")
    private LocalDate CFechaBaja;
}
