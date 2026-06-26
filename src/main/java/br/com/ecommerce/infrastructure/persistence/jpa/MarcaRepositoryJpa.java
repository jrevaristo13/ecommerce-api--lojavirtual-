package br.com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MarcaRepositoryJpa extends JpaRepository<MarcaEntity, UUID> {
    Optional<MarcaEntity> findByNome(String nome);
}
