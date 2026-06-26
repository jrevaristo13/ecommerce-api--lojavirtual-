package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListarSkusUseCase {
    private final SkuRepository skuRepository;

    public ListarSkusUseCase(SkuRepository skuRepository) {
        this.skuRepository = skuRepository;
    }

    public List<Sku> executar() {
        return skuRepository.findAll();
    }
}
