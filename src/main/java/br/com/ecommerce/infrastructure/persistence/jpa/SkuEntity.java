package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.Dimensao;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "skus")
public class SkuEntity {
    
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;
    
    @Column(nullable = false)
    private String nomeVariacao;
    
    @Column(nullable = false)
    private BigDecimal preco;
    
    @Column(nullable = false)
    private Integer quantidadeEstoque;
    
    @Column(nullable = false)
    private boolean ativo = true;
    
    // ✅ NOVO: Dimensão embutida
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "altura", column = @Column(name = "altura", precision = 5, scale = 2)),
        @AttributeOverride(name = "largura", column = @Column(name = "largura", precision = 5, scale = 2)),
        @AttributeOverride(name = "profundidade", column = @Column(name = "profundidade", precision = 5, scale = 2)),
        @AttributeOverride(name = "peso", column = @Column(name = "peso", precision = 8, scale = 3))
    })
    private Dimensao dimensao;
    
    public SkuEntity() {}
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public ProdutoEntity getProduto() { return produto; }
    public void setProduto(ProdutoEntity produto) { this.produto = produto; }
    
    public String getNomeVariacao() { return nomeVariacao; }
    public void setNomeVariacao(String nomeVariacao) { this.nomeVariacao = nomeVariacao; }
    
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    
    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(Integer quantidadeEstoque) { this.quantidadeEstoque = quantidadeEstoque; }
    
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    
    public Dimensao getDimensao() { return dimensao; }
    public void setDimensao(Dimensao dimensao) { this.dimensao = dimensao; }
}
