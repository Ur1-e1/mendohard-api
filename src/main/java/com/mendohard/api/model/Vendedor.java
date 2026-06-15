package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Entity
@Table(name = "vendedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Vendedor")
public class Vendedor extends Usuario {

    @Column(name = "v_telefono")
    private String VTelefono;

    @Column(name = "v_cuit", nullable = false, unique = true)
    private String VCuit;

    @Column(name = "v_razon_social", nullable = false)
    private String VRazonSocial;

    @Column(name = "v_categoria_fiscal")
    private String VCategoriaFiscal;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vendedor_id")
    private List<VendedorEstado> vendedorEstados;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "vendedor_id")
    private List<Comercio> comercios;
}
