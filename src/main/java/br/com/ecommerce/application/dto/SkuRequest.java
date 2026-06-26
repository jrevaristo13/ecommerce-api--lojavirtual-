package br.com.ecommerce.application.dto;

import jakarta.validation.constraints.*; // IMPORTANTE: Use jakarta
import java.math.BigDecimal;
import java.util.UUID;
import br.com.ecommerce.domain.Dimensao;

public record SkuRequest(
    @NotNull(message = "ID do produto é obrigatório")
    UUID produtoId,

    @NotBlank(message = "Código é obrigatório")
    String codigoSku,

    @NotBlank(message = "Nome é obrigatório")
    String nomeVariacao,

    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    BigDecimal preco,

    @Min(value = 0, message = "Estoque não pode ser negativo")
    int quantidadeEstoque,

    Dimensao dimensao
) {}
