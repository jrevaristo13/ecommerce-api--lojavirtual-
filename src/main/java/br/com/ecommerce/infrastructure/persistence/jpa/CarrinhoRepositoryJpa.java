package br.com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarrinhoRepositoryJpa extends JpaRepository<CarrinhoEntity, UUID> {
    Optional<CarrinhoEntity> findByClienteId(UUID clienteId);
    void deleteByClienteId(UUID clienteId);
}
