package br.com.ecommerce.application.dto;

import java.util.UUID;

public record ItemCompraDTO(UUID skuId, int quantidade) {
}
