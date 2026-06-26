package br.com.ecommerce.infrastructure.configuration;

import br.com.ecommerce.application.repository.CarrinhoRepository;
import br.com.ecommerce.application.repository.PedidoRepository;
import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.application.usecase.AdicionarItemAoCarrinhoUseCase;
import br.com.ecommerce.application.usecase.FecharPedidoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjectionConfig {

    @Bean
    public FecharPedidoUseCase fecharPedidoUseCase(
            CarrinhoRepository carrinhoRepository,
            PedidoRepository pedidoRepository,
            SkuRepository skuRepository) {
        return new FecharPedidoUseCase(carrinhoRepository, pedidoRepository, skuRepository);
    }

    @Bean
    public AdicionarItemAoCarrinhoUseCase adicionarItemAoCarrinhoUseCase(
            CarrinhoRepository carrinhoRepository,
            SkuRepository skuRepository) {
        return new AdicionarItemAoCarrinhoUseCase(carrinhoRepository, skuRepository);
    }
}
