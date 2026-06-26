package br.com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoriaRepositoryJpa extends JpaRepository<CategoriaEntity, UUID> {
    List<CategoriaEntity> findByCategoriaPaiId(UUID categoriaPaiId);
    
    @Query("SELECT c FROM CategoriaEntity c WHERE c.categoriaPaiId IS NULL")
    List<CategoriaEntity> findRaizes();
}
