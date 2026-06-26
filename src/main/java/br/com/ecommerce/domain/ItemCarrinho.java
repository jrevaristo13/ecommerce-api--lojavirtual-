package br.com.ecommerce.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public class ItemCarrinho {
    
    private final UUID produtoId;
    private final String nomeProduto;
    private int quantidade;
    private final BigDecimal precoUnitario;

    public ItemCarrinho(UUID produtoId, String nomeProduto, int quantidade, BigDecimal precoUnitario) {
        this.produtoId = Objects.requireNonNull(produtoId, "O ID do produto não pode ser nulo.");
        this.nomeProduto = Objects.requireNonNull(nomeProduto, "O nome do produto não pode ser nulo.");
        
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço unitário deve ser maior que zero.");
        }
        
        validarQuantidade(quantidade);
        
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario.setScale(2, RoundingMode.HALF_EVEN);
    }

    public void alterarQuantidade(int novaQuantidade) {
        validarQuantidade(novaQuantidade);
        this.quantidade = novaQuantidade;
    }

    public BigDecimal calcularValorTotalItem() {
        return this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade)).setScale(2, RoundingMode.HALF_EVEN);
    }

    private void validarQuantidade(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("A quantidade do item deve ser maior que zero.");
        }
    }
    // ==========================================
    //             OBJECT METHODS
    // ==========================================

    @Override
    public boolean equals(Object obj) {
        // 1. Verificação de referência em memória
        if (this == obj) return true;
        
        // 2. Verificação de nulidade e igualdade exata de classe
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // 3. Cast para o tipo correto
        ItemCarrinho other = (ItemCarrinho) obj;
        
        // 4. Como é um Value Object, a igualdade depende dos valores dos atributos de negócio
        return Objects.equals(this.produtoId, other.produtoId) &&
               Objects.equals(this.precoUnitario, other.precoUnitario);
    }

    @Override
    public int hashCode() {
        // O hash acompanha os mesmos atributos utilizados no equals
        return Objects.hash(this.produtoId, this.precoUnitario);
    }

    @Override
    public String toString() {
        return "ItemCarrinho{" +
                "produtoId=" + produtoId +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                ", totalItem=" + calcularValorTotalItem() +
                '}';
    }

    public UUID getProdutoId() { return produtoId; }
    public String getNomeProduto() { return nomeProduto; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
}
