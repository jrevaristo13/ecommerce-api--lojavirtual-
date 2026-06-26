package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SkuMapper {
    
    public Sku toDomain(SkuEntity entity) {
        if (entity == null) return null;
        
        UUID produtoId = (entity.getProduto() != null) ? entity.getProduto().getId() : null;
        
        return new Sku(
            entity.getId(),
            produtoId,
            "COD-" + entity.getId().toString().substring(0, 8).toUpperCase(),
            entity.getNomeVariacao(),
            entity.getPreco(),
            entity.getQuantidadeEstoque(),
            entity.isAtivo(),
            entity.getDimensao()  // ✅ Usa a dimensão real da entidade
        );
    }
    
    public SkuEntity toEntity(Sku sku) {
        if (sku == null) return null;
        
        SkuEntity entity = new SkuEntity();
        entity.setId(sku.getId());
        entity.setNomeVariacao(sku.getNomeVariacao());
        entity.setPreco(sku.getPreco());
        entity.setQuantidadeEstoque(sku.getQuantidadeEstoque());
        entity.setAtivo(sku.isAtivo());
        entity.setDimensao(sku.getDimensao());  // ✅ Mapeia a dimensão
        
        if (sku.getProdutoId() != null) {
            ProdutoEntity produtoEntity = new ProdutoEntity();
            produtoEntity.setId(sku.getProdutoId());
            entity.setProduto(produtoEntity);
        }
        
        return entity;
    }
}
