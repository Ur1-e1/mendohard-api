package com.mendohard.api.repository;


import com.mendohard.api.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    @Query("SELECT d FROM Departamento d WHERE d.DCodigo = :codigo")
    Optional<Departamento> findByCodigoDepartamento(@Param("codigo") String codigo);

    @Query("SELECT d FROM Departamento d WHERE d.DFechaBaja IS NULL")
    List<Departamento> findAllActivos();
}
