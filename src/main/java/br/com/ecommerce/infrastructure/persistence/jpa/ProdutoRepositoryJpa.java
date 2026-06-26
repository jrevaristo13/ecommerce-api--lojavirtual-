package br.com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ProdutoRepositoryJpa extends JpaRepository<ProdutoEntity, UUID> {
    // Como estendemos JpaRepository, você não precisa escrever métodos aqui.
    // O Spring fornecerá automaticamente:
    // save(), findById(), findAll(), deleteById(), existsById(), etc.
}