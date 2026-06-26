package br.com.ecommerce.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "produtos")
public class ProdutoEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private UUID marcaId;

    private boolean ativo;

    // Relacionamento Um-para-Muitos: Um Produto tem vários SKUs
    // O cascade ALL garante que, ao salvar o produto, os SKUs sejam salvos juntos
    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkuEntity> skus = new ArrayList<>();

    // Construtor padrão exigido pelo JPA
    public ProdutoEntity() {}

    // Construtor para criação completa
    public ProdutoEntity(UUID id, String nome, UUID marcaId, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.marcaId = marcaId;
        this.ativo = ativo;
    }

    // Métodos auxiliares de controle de coleção (Boas práticas de DDD)
    public void addSku(SkuEntity sku) {
        skus.add(sku);
        sku.setProduto(this);
    }

    public void removeSku(SkuEntity sku) {
        skus.remove(sku);
        sku.setProduto(null);
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public UUID getMarcaId() { return marcaId; }
    public void setMarcaId(UUID marcaId) { this.marcaId = marcaId; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public List<SkuEntity> getSkus() { return skus; }
    public void setSkus(List<SkuEntity> skus) { this.skus = skus; }
}
