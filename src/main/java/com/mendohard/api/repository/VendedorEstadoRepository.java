package com.mendohard.api.repository;


import com.mendohard.api.model.VendedorEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorEstadoRepository extends JpaRepository<VendedorEstado, Long> {
}
