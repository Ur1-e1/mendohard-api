package com.mendohard.api.repository;

import com.mendohard.api.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Buscar todas las categorías activas (CFechaBaja IS NULL)
    List<Categoria> findByCFechaBajaIsNull();

    // Buscar categoría por su código de negocio (CCodigo)
    Optional<Categoria> findByCCodigo(String cCodigo);

    // Buscar categoría por código de negocio y que esté activa
    Optional<Categoria> findByCCodigoAndCFechaBajaIsNull(String cCodigo);

    // Obtener únicamente categorías activas que no tengan subcategorías activas (hojas)
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Categoria c WHERE c.CFechaBaja IS NULL AND NOT EXISTS (SELECT sub FROM Categoria sub WHERE sub.categoriaPadre = c AND sub.CFechaBaja IS NULL)")
    List<Categoria> findCategoriasHojasActivas();
}
