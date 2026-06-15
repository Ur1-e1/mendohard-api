package com.mendohard.api.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "responsable_mendo_hard")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("Responsable MendoHard")
public class ResponsableMendoHard extends Usuario {

    @Column(name = "rmh_legajo", nullable = false, unique = true)
    private String RMHLegajo;
}
