package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
    private String telefono;

    @Column(name = "v_cuit", nullable = false, unique = true)
    private String cuit;

    @Column(name = "v_razon_social", nullable = false)
    private String razonSocial;

    @Column(name = "v_categoria_fiscal")
    private String categoriaFiscal;
}
