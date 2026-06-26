package br.com.ecommerce.application.dto;

import java.util.UUID;

public record AdicionarItemDTO(UUID clienteId, UUID skuId, int quantidade) {}
