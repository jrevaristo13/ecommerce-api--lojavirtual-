package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class DesativarSkuUseCase {

    private final SkuRepository skuRepository;

    public DesativarSkuUseCase(SkuRepository skuRepository) {
        this.skuRepository = skuRepository;
    }

    @Transactional
    public void executar(UUID skuId) {
        // 1. Busca o SKU através do repositório
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new IllegalArgumentException("SKU não encontrado com ID: " + skuId));
        
        // 2. Aplica a regra de negócio (Inativação)
        sku.inativar();
        
        // 3. Persiste a alteração
        skuRepository.save(sku);
    }
}
