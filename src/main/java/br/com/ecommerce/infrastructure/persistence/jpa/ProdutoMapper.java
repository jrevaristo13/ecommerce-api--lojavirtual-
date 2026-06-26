package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.Dimensao;
import br.com.ecommerce.domain.Produto;
import br.com.ecommerce.domain.Sku;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProdutoMapper {

    // ==========================================
    // PRODUTO
    // ==========================================

    public ProdutoEntity toEntity(Produto domain) {
        if (domain == null) {
            return null;
        }

        ProdutoEntity entity = new ProdutoEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setMarcaId(domain.getMarcaId());
        entity.setAtivo(domain.isAtivo());
        return entity;
    }

    public Produto toDomain(ProdutoEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Produto(
                entity.getId(),
                entity.getNome(),
                entity.getMarcaId(),
                entity.isAtivo()
        );
    }

    // ==========================================
    // SKU
    // ==========================================

    public void updateEntityWithSku(SkuEntity entity, Sku sku) {
        if (entity == null || sku == null) {
            return;
        }

        entity.setNomeVariacao(sku.getNomeVariacao());
        entity.setPreco(sku.getPreco());
        entity.setQuantidadeEstoque(sku.getQuantidadeEstoque());
        entity.setAtivo(sku.isAtivo());
        entity.setDimensao(sku.getDimensao());
    }

    public SkuEntity toSkuEntity(Sku sku) {
        if (sku == null) {
            return null;
        }

        SkuEntity entity = new SkuEntity();
        entity.setId(sku.getId());
        entity.setNomeVariacao(sku.getNomeVariacao());
        entity.setPreco(sku.getPreco());
        entity.setQuantidadeEstoque(sku.getQuantidadeEstoque());
        entity.setAtivo(sku.isAtivo());
        entity.setDimensao(sku.getDimensao());

        return entity;
    }

    public Sku toSkuDomain(SkuEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Sku(
                entity.getId(),
                entity.getProduto() != null ? entity.getProduto().getId() : null,
                "COD-" + entity.getId().toString().substring(0, 8).toUpperCase(),
                entity.getNomeVariacao(),
                entity.getPreco(),
                entity.getQuantidadeEstoque(),
                entity.isAtivo(),
                entity.getDimensao() != null ? entity.getDimensao() : new Dimensao(
                    BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE
                )
        );
    }
}
