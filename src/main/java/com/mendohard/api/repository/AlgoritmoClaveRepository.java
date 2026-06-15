package com.mendohard.api.repository;

import com.mendohard.api.model.AlgoritmoClave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlgoritmoClaveRepository extends JpaRepository<AlgoritmoClave, Long> {
}
