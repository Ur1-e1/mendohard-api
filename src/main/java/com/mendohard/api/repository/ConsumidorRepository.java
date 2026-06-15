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
}
