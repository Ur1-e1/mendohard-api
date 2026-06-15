package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clave")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "c_codigo", nullable = false, unique = true)
    private String codigo;

    @Column(name = "c_contrasena", nullable = false)
    private String contraseña;

    @Column(name = "c_salt", nullable = false)
    private String salt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;
}
