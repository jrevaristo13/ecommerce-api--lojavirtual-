package br.com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepositoryJpa extends JpaRepository<PedidoEntity, UUID> {
    
    @Query("SELECT p FROM PedidoEntity p LEFT JOIN FETCH p.itens WHERE p.id = :id")
    Optional<PedidoEntity> findByIdComItens(@Param("id") UUID id);
}
