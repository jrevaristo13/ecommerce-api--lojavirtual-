package br.com.ecommerce.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AdicionarItemDTO(
    @NotNull(message = "Cliente ID é obrigatório")
    UUID clienteId,
    
    @NotNull(message = "SKU ID é obrigatório")
    UUID skuId,
    
    @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    int quantidade
) {}
