package br.com.ecommerce.application.dto;

import br.com.ecommerce.domain.Dimensao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record SkuRequest(
    @NotNull(message = "Produto ID é obrigatório")
    UUID produtoId,
    
    @NotBlank(message = "Código SKU é obrigatório")
    String codigoSku,
    
    @NotBlank(message = "Nome da variação é obrigatório")
    String nomeVariacao,
    
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    BigDecimal preco,
    
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
    Integer quantidadeEstoque,
    
    Boolean ativo,
    
    DimensaoRequest dimensao
) {
    public record DimensaoRequest(
        Double peso,
        Double altura,
        Double largura,
        Double comprimento
    ) {
        public Dimensao toDimensao() {
            if (peso == null || altura == null || largura == null || comprimento == null) {
                return null;
            }
            return new Dimensao(
                BigDecimal.valueOf(peso),
                BigDecimal.valueOf(altura),
                BigDecimal.valueOf(largura),
                BigDecimal.valueOf(comprimento)
            );
        }
    }
}
