package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class SkuRepositoryJpaImpl implements SkuRepository {
    
    private final SkuJpaRepository skuJpaRepository;
    private final SkuMapper skuMapper;
    
    public SkuRepositoryJpaImpl(SkuJpaRepository skuJpaRepository, SkuMapper skuMapper) {
        this.skuJpaRepository = skuJpaRepository;
        this.skuMapper = skuMapper;
    }
    
    @Override
    public Optional<Sku> findById(UUID id) {
        return skuJpaRepository.findById(id)
                .map(skuMapper::toDomain);
    }
    
    @Override
    public Sku save(Sku sku) {
        SkuEntity entity = skuMapper.toEntity(sku);
        SkuEntity savedEntity = skuJpaRepository.save(entity);
        return skuMapper.toDomain(savedEntity);
    }
    
    @Override
    public List<Sku> findAll() {
        return skuJpaRepository.findAll()
                .stream()
                .map(skuMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID id) {
        skuJpaRepository.deleteById(id);
    }
}
