package com.mendohard.api.repository;


import com.mendohard.api.model.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {

    @Query("SELECT p FROM Provincia p WHERE p.ProFechaBaja IS NULL")
    List<Provincia> findAllActivas();
}