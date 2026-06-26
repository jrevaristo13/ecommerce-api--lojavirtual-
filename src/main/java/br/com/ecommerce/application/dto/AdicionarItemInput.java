package br.com.ecommerce.application.dto;

import java.util.UUID;

public record AdicionarItemInput(
    UUID clienteId,
    UUID skuId,
    int quantidade
) {
    // Validação defensiva direto no construtor do Record
    public AdicionarItemInput {
        if (clienteId == null) throw new IllegalArgumentException("O ID do cliente é obrigatório.");
        if (skuId == null) throw new IllegalArgumentException("O ID do SKU é obrigatório.");
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
    }
}
