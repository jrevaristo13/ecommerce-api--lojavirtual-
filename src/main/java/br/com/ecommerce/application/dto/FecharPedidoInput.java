package br.com.ecommerce.application.dto;

import java.util.UUID;

public record FecharPedidoInput(
    UUID clienteId,
    String formaPagamentoId, // Ex: "PIX", "CARTAO_CREDITO"
    UUID enderecoEntregaId
) {
    public FecharPedidoInput {
        if (clienteId == null) throw new IllegalArgumentException("O ID do cliente é obrigatório.");
        if (formaPagamentoId == null || formaPagamentoId.isBlank()) throw new IllegalArgumentException("A forma de pagamento é obrigatória.");
        if (enderecoEntregaId == null) throw new IllegalArgumentException("O endereço de entrega é obrigatório.");
    }
}
