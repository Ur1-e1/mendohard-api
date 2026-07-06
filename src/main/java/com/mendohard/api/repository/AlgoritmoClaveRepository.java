package com.mendohard.api.repository;

import com.mendohard.api.model.AlgoritmoClave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlgoritmoClaveRepository extends JpaRepository<AlgoritmoClave, Long> {

    @Query("SELECT ac FROM AlgoritmoClave ac WHERE ac.ACNombre = :nombre AND ac.ACFechaBaja IS NULL")
    Optional<AlgoritmoClave> findByACNombreActivo(@Param("nombre") String nombre);
}
