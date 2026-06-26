package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.dto.ItemCompraDTO;
import br.com.ecommerce.application.repository.PedidoRepository;
import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CriarPedidoUseCase {

    private final SkuRepository skuRepository;
    private final PedidoRepository pedidoRepository;

    public CriarPedidoUseCase(SkuRepository skuRepository, PedidoRepository pedidoRepository) {
        this.skuRepository = skuRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public void executar(String cliente, List<ItemCompraDTO> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item.");
        }

        Pedido pedido = new Pedido(cliente);

        for (ItemCompraDTO itemDto : itens) {
            Sku sku = skuRepository.findById(itemDto.skuId())
                .orElseThrow(() -> new IllegalArgumentException("SKU não encontrado: " + itemDto.skuId()));

            sku.debitarEstoque(itemDto.quantidade()); 
            
            ItemPedido item = new ItemPedido(
                sku.getProdutoId(), 
                sku.getNomeVariacao(), 
                sku.getPreco(), 
                itemDto.quantidade()
            );
            pedido.adicionarItem(item);
            
            skuRepository.save(sku);
        }

        pedidoRepository.save(pedido);
    }
}
