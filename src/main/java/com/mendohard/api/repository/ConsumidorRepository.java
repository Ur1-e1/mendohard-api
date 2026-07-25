package com.mendohard.api.repository;


import com.mendohard.api.model.Consumidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumidorRepository extends JpaRepository<Consumidor, Long> {

    @Query("SELECT c FROM Consumidor c WHERE c.UEmail = :email AND c.UFechaBaja IS NULL")
    Optional<Consumidor> findByEmailActivo(@Param("email") String email);

    @Query("SELECT c FROM Consumidor c WHERE c.CApodo = :apodo AND c.UFechaBaja IS NULL")
    Optional<Consumidor> findByApodoActivo(@Param("apodo") String apodo);

    // CU-04: Verificar unicidad de apodo en consumidores activos, excluyendo al consumidor en sesión
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Consumidor c " +
            "WHERE c.UFechaBaja IS NULL AND c.CApodo = :apodo AND c.id <> :id")
    boolean existsByCApodoAndUFechaBajaIsNullAndIdNot(@Param("apodo") String apodo, @Param("id") Long id);
}
