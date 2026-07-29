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

    @Query("SELECT d FROM Departamento d JOIN FETCH d.provincia p JOIN FETCH p.pais pa " +
           "WHERE d.DFechaBaja IS NULL AND p.ProFechaBaja IS NULL AND pa.PFechaBaja IS NULL")
    List<Departamento> findAllActivosCascade();

    @Query("SELECT d FROM Departamento d WHERE d.DCodigo = :dCodigo AND d.DFechaBaja IS NULL")
    Optional<Departamento> findByDCodigoAndDFechaBajaIsNull(@Param("dCodigo") String dCodigo);
}
