package br.com.ecommerce.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SkuJpaRepository extends JpaRepository<SkuEntity, UUID> {
    
    // Métodos personalizados podem ser adicionados aqui
    // Exemplo: findByCodigo(String codigo);
    // Exemplo: findByProdutoId(UUID produtoId);
}
