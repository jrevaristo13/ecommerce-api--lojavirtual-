package br.com.ecommerce.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Sku {
    private final UUID id;
    private final UUID produtoId;
    private final String codigoSku;
    private final String nomeVariacao;
    private final BigDecimal preco;
    private Integer quantidadeEstoque; 
    private boolean ativo;
    private final Dimensao dimensao;

    // Construtor completo
    public Sku(UUID id, UUID produtoId, String codigoSku, String nomeVariacao, 
               BigDecimal preco, Integer quantidadeEstoque, boolean ativo, Dimensao dimensao) {
        this.id = Objects.requireNonNull(id, "ID do SKU é obrigatório");
        this.produtoId = Objects.requireNonNull(produtoId, "ID do Produto é obrigatório");
        this.codigoSku = Objects.requireNonNull(codigoSku, "Código SKU é obrigatório");
        this.nomeVariacao = Objects.requireNonNull(nomeVariacao, "Nome da variação é obrigatório");
        this.preco = Objects.requireNonNull(preco, "Preço é obrigatório");
        this.quantidadeEstoque = Objects.requireNonNull(quantidadeEstoque, "Estoque é obrigatório");
        this.ativo = ativo;
        this.dimensao = dimensao;
    }

    // Construtor simplificado para facilitar a criação via Mapper ou Controller
    public Sku(UUID id, UUID produtoId, String codigoSku, String nomeVariacao, 
               BigDecimal preco, Integer quantidadeEstoque, Dimensao dimensao) {
        this(id, produtoId, codigoSku, nomeVariacao, preco, quantidadeEstoque, true, dimensao);
    }

    // --- Métodos de Domínio ---
    public void atualizarEstoque(Integer novaQuantidade) {
        if (novaQuantidade == null || novaQuantidade < 0) {
            throw new IllegalArgumentException("A quantidade de estoque não pode ser nula ou negativa.");
        }
        this.quantidadeEstoque = novaQuantidade;
    }

    public void debitarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a ser debitada deve ser maior que zero.");
        }
        if (this.quantidadeEstoque < quantidade) {
            throw new IllegalStateException("Estoque insuficiente para o SKU: " + this.codigoSku);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public void inativar() {
        if (!this.ativo) {
            throw new IllegalStateException("O SKU já está inativo.");
        }
        this.ativo = false;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getProdutoId() { return produtoId; }
    public String getCodigoSku() { return codigoSku; }
    public String getNomeVariacao() { return nomeVariacao; }
    public BigDecimal getPreco() { return preco; }
    public Integer getQuantidadeEstoque() { return quantidadeEstoque; }
    public boolean isAtivo() { return ativo; }
    public Dimensao getDimensao() { return dimensao; }
}
