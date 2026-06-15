package com.mendohard.api.repository;


import com.mendohard.api.model.ComercioEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComercioEstadoRepository extends JpaRepository<ComercioEstado, Long> {
}
