package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.repository.SkuRepository;
import br.com.ecommerce.domain.Dimensao;
import br.com.ecommerce.domain.Sku;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarEstoqueUseCaseTest {

    @Mock
    private SkuRepository skuRepository;

    private AtualizarEstoqueUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AtualizarEstoqueUseCase(skuRepository);
    }

    private Sku criarSkuValido(int estoque) {
        return new Sku(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "SKU-001",
            "Camiseta M",
            new BigDecimal("99.90"),
            estoque,
            true,
            new Dimensao(new BigDecimal("2"), new BigDecimal("30"), new BigDecimal("20"), new BigDecimal("0.250"))
        );
    }

    @Test
    @DisplayName("Deve atualizar estoque com sucesso")
    void deveAtualizarEstoqueComSucesso() {
        // Arrange
        UUID skuId = UUID.randomUUID();
        Sku sku = criarSkuValido(10);
        when(skuRepository.findById(skuId)).thenReturn(Optional.of(sku));
        when(skuRepository.save(any(Sku.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        useCase.executar(skuId, 50);

        // Assert
        verify(skuRepository).findById(skuId);
        verify(skuRepository).save(any(Sku.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando SKU não existe")
    void deveLancarExcecaoQuandoSkuNaoExiste() {
        // Arrange
        UUID skuId = UUID.randomUUID();
        when(skuRepository.findById(skuId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.executar(skuId, 50));
        verify(skuRepository, never()).save(any(Sku.class));
    }
}
