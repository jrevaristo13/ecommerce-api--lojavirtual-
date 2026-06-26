package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Optional;

@Service
public class BuscarSkuUseCase {

    private final SkuRepository skuRepository;

    public BuscarSkuUseCase(SkuRepository skuRepository) {
        this.skuRepository = skuRepository;
    }

    public Optional<Sku> executar(UUID id) {
        return skuRepository.findById(id);
    }
}
