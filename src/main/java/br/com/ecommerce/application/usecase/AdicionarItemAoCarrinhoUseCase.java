package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.dto.AdicionarItemInput;
import br.com.ecommerce.application.repository.CarrinhoRepository;
import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Carrinho;
import br.com.ecommerce.domain.ItemCarrinho;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Service;

@Service
public class AdicionarItemAoCarrinhoUseCase {

    private final CarrinhoRepository carrinhoRepository;
    private final SkuRepository skuRepository;

    public AdicionarItemAoCarrinhoUseCase(CarrinhoRepository carrinhoRepository, SkuRepository skuRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.skuRepository = skuRepository;
    }

    public void executar(AdicionarItemInput input) {
        
        // 1. Busca a variação exata (SKU) que o cliente quer comprar
        Sku sku = skuRepository.findById(input.skuId())
                .orElseThrow(() -> new IllegalArgumentException("Produto/SKU não encontrado."));

        // 2. Valida se o SKU está ativo e se tem estoque real antes de prometer no carrinho
        if (!sku.isAtivo()) {
            throw new IllegalStateException("Este item não está mais disponível para venda.");
        }
        if (sku.getQuantidadeEstoque() < input.quantidade()) {
            throw new IllegalStateException("Estoque insuficiente para o item selecionado. Disponível: " + sku.getQuantidadeEstoque());
        }

        // 3. Busca o carrinho do cliente no banco/cache. Se não existir, cria um novo.
        Carrinho carrinho = carrinhoRepository.buscarPorClienteId(input.clienteId())
                .orElseGet(() -> new Carrinho(input.clienteId())); 

        // 4. Monta o Item do Carrinho usando os dados blindados do Sku
        ItemCarrinho novoItem = new ItemCarrinho(
                sku.getId(),
                sku.getNomeVariacao(),
                input.quantidade(), 
                sku.getPreco()
        );

        // 5. Delega para a Entidade de Domínio a regra de somar e recalcular os totais
        carrinho.adicionarItem(novoItem);

        // 6. Persiste o estado atualizado
        carrinhoRepository.salvar(carrinho);
    }
}
