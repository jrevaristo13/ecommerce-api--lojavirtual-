package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.dto.FecharPedidoInput;
import br.com.ecommerce.application.repository.CarrinhoRepository;
import br.com.ecommerce.application.repository.PedidoRepository;
import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Carrinho;
import br.com.ecommerce.domain.ItemPedido;
import br.com.ecommerce.domain.Pedido;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FecharPedidoUseCase {

    private final CarrinhoRepository carrinhoRepository;
    private final PedidoRepository pedidoRepository;
    private final SkuRepository skuRepository;

    public FecharPedidoUseCase(
            CarrinhoRepository carrinhoRepository, 
            PedidoRepository pedidoRepository, 
            SkuRepository skuRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.pedidoRepository = pedidoRepository;
        this.skuRepository = skuRepository;
    }

    @Transactional
    public void executar(FecharPedidoInput input) {
        // 1. Busca o carrinho
        Carrinho carrinho = carrinhoRepository.buscarPorClienteId(input.clienteId())
                .orElseThrow(() -> new IllegalStateException("Nenhum carrinho ativo encontrado."));

        // 2. Finaliza carrinho
        carrinho.finalizarCarrinho();

        // 3. Inicia o Pedido
        String nomeCliente = "Cliente ID: " + carrinho.getClienteId().toString().substring(0, 8);
        Pedido pedido = new Pedido(nomeCliente);

        // 4. Processa itens
        for (var itemCarrinho : carrinho.getItens()) {
            Sku sku = skuRepository.findById(itemCarrinho.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("SKU não encontrado."));
            
            // Debita estoque no domínio
            sku.debitarEstoque(itemCarrinho.getQuantidade());

            // Construtor correto: (UUID produtoId, String nomeProduto, BigDecimal precoUnitario, int quantidade)
            ItemPedido itemPedido = new ItemPedido(
                sku.getProdutoId(), 
                sku.getNomeVariacao(), 
                sku.getPreco(), 
                itemCarrinho.getQuantidade()
            );
            
            pedido.adicionarItem(itemPedido);
            
            // Persiste baixa no estoque
            skuRepository.save(sku); 
        }

        // 5. Persiste estados finais
        pedidoRepository.save(pedido);
        carrinhoRepository.save(carrinho);
    }
}
