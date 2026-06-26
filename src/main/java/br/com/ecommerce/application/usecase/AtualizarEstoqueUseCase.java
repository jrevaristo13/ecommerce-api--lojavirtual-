package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class AtualizarEstoqueUseCase {

    private final SkuRepository skuRepository;

    public AtualizarEstoqueUseCase(SkuRepository skuRepository) {
        this.skuRepository = skuRepository;
    }

    @Transactional
    public void executar(UUID id, Integer novaQuantidade) {
        // 1. Busca o Sku no banco
        Sku sku = skuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SKU não encontrado com ID: " + id));
        
        // 2. Chama o método que criamos na classe Sku (isso resolve seu erro!)
        sku.atualizarEstoque(novaQuantidade);
        
        // 3. Salva a alteração
        skuRepository.save(sku);
    }
}