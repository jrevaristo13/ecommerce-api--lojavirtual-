package br.com.ecommerce.application.repository;

import br.com.ecommerce.domain.Sku;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkuRepository {
    Optional<Sku> findById(UUID id);
    Sku save(Sku sku);
    List<Sku> findAll();
    void deleteById(UUID id);
}
