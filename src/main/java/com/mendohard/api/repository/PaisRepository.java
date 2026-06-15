package com.mendohard.api.repository;


import com.mendohard.api.model.Pais;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

    @Query("SELECT p FROM Pais p WHERE p.PFechaBaja IS NULL")
    List<Pais> findAllActivos();
}
