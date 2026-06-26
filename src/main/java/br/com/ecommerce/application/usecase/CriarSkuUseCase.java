package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriarSkuUseCase {

    private final SkuRepository skuRepository;

    public CriarSkuUseCase(SkuRepository skuRepository) {
        this.skuRepository = skuRepository;
    }

    @Transactional
    public void executar(Sku sku) {
        // Validação adicional: garante que não salvaremos algo nulo por acidente
        if (sku == null) {
            throw new IllegalArgumentException("O objeto SKU não pode ser nulo.");
        }

        // Você poderia adicionar aqui uma lógica: 
        // if (skuRepository.existsByCodigoSku(sku.getCodigoSku())) { ... }

        skuRepository.save(sku); 
    }
}
