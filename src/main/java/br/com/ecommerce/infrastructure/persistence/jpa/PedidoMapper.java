package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.ItemPedido;
import br.com.ecommerce.domain.Pedido;
import br.com.ecommerce.domain.StatusPedido;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PedidoMapper {
    
    public PedidoEntity toEntity(Pedido domain) {
        if (domain == null) return null;
        
        PedidoEntity entity = new PedidoEntity();
        entity.setId(domain.getId());
        entity.setCliente(domain.getCliente());
        entity.setStatus(domain.getStatus());
        entity.setValorTotal(domain.getValorTotal());
        entity.setDataCriacao(domain.getDataCriacao());
        
        // Mapear itens com referência bidirecional
        List<ItemPedidoEntity> itemEntities = new ArrayList<>();
        if (domain.getItens() != null) {
            for (ItemPedido item : domain.getItens()) {
                ItemPedidoEntity itemEntity = new ItemPedidoEntity();
                itemEntity.setId(item.getId());
                itemEntity.setPedido(entity); // ⭐ Referência bidirecional
                itemEntity.setProdutoId(item.getProdutoId());
                itemEntity.setNomeProduto(item.getNomeProduto());
                itemEntity.setPrecoUnitario(item.getPrecoUnitario());
                itemEntity.setQuantidade(item.getQuantidade());
                itemEntity.setSubtotal(item.getSubtotal());
                itemEntities.add(itemEntity);
            }
        }
        entity.setItens(itemEntities);
        
        return entity;
    }
    
    public Pedido toDomain(PedidoEntity entity) {
        if (entity == null) return null;
        
        // ⭐ USAR CONSTRUTOR DE RECONSTITUIÇÃO (mantém o ID original!)
        List<ItemPedido> itens = new ArrayList<>();
        if (entity.getItens() != null) {
            for (ItemPedidoEntity itemEntity : entity.getItens()) {
                ItemPedido item = new ItemPedido(
                    itemEntity.getId(),
                    itemEntity.getProdutoId(),
                    itemEntity.getNomeProduto(),
                    itemEntity.getPrecoUnitario(),
                    itemEntity.getQuantidade(),
                    itemEntity.getSubtotal()
                );
                itens.add(item);
            }
        }
        
        return new Pedido(
            entity.getId(),
            entity.getCliente(),
            itens,
            entity.getStatus(),
            entity.getValorTotal(),
            entity.getDataCriacao()
        );
    }
}
