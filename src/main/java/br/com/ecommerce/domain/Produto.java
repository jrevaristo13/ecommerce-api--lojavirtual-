package br.com.ecommerce.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Produto {
    
    private final UUID id;
    private final String nome;
    private final UUID marcaId;
    private final boolean ativo;
    private final List<Sku> skus = new ArrayList<>();

    public Produto(UUID id, String nome, UUID marcaId, boolean ativo) {
        this.id = Objects.requireNonNull(id, "ID é obrigatório");
        this.nome = Objects.requireNonNull(nome, "Nome é obrigatório");
        this.marcaId = Objects.requireNonNull(marcaId, "MarcaID é obrigatório");
        this.ativo = ativo;
    }

    public void adicionarSku(Sku novoSku) {
        Objects.requireNonNull(novoSku, "O SKU não pode ser nulo.");
        
        boolean codigoJaExiste = this.skus.stream()
                .anyMatch(s -> s.getCodigoSku().equalsIgnoreCase(novoSku.getCodigoSku()));
                
        if (codigoJaExiste) {
            throw new IllegalArgumentException("Já existe um SKU com o código " + novoSku.getCodigoSku());
        }
        
        this.skus.add(novoSku);
    }

    public Optional<Sku> getSkuPorId(UUID skuId) {
        return this.skus.stream()
                .filter(s -> s.getId().equals(skuId))
                .findFirst();
    }
    
    
    // Encapsulamento profissional: retorna uma cópia imutável
    public List<Sku> getSkus() {
        return Collections.unmodifiableList(this.skus);
    }

    // Getters básicos
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public UUID getMarcaId() { return marcaId; }
    public boolean isAtivo() { return ativo; }
}