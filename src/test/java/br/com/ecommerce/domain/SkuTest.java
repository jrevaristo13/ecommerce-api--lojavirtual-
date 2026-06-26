package br.com.ecommerce.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Testes unitários da entidade Sku.
 * 
 * Conceito: Testes unitários verificam UMA classe isolada,
 * sem dependências externas (banco, API, etc).
 */
class SkuTest {

    // ========================================
    // HELPER: Método para criar um SKU válido
    // ========================================
    private Sku criarSkuValido() {
        return new Sku(
            UUID.randomUUID(),
            UUID.randomUUID(),
            "SKU-001",
            "Camiseta M Azul",
            new BigDecimal("99.90"),
            10,
            true,
            new Dimensao(
                new BigDecimal("2"),
                new BigDecimal("30"),
                new BigDecimal("20"),
                new BigDecimal("0.250")
            )
        );
    }

    // ========================================
    // TESTES DE DÉBITO DE ESTOQUE
    // ========================================

    @Test
    @DisplayName("Deve debitar estoque corretamente quando há quantidade suficiente")
    void deveDebitarEstoqueComSucesso() {
        // Arrange (Preparação)
        Sku sku = criarSkuValido();
        int quantidadeAntes = sku.getQuantidadeEstoque();

        // Act (Ação)
        sku.debitarEstoque(3);

        // Assert (Verificação)
        assertEquals(quantidadeAntes - 3, sku.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar debitar mais do que tem em estoque")
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        // Arrange
        Sku sku = criarSkuValido();

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> sku.debitarEstoque(100)
        );

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar debitar quantidade zero ou negativa")
    void deveLancarExcecaoQuandoQuantidadeInvalida() {
        // Arrange
        Sku sku = criarSkuValido();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sku.debitarEstoque(0));
        assertThrows(IllegalArgumentException.class, () -> sku.debitarEstoque(-5));
    }

    // ========================================
    // TESTES DE ATUALIZAÇÃO DE ESTOQUE
    // ========================================

    @Test
    @DisplayName("Deve atualizar estoque com quantidade válida")
    void deveAtualizarEstoqueComSucesso() {
        // Arrange
        Sku sku = criarSkuValido();

        // Act
        sku.atualizarEstoque(50);

        // Assert
        assertEquals(50, sku.getQuantidadeEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar estoque com valor negativo")
    void deveLancarExcecaoAoAtualizarComValorNegativo() {
        // Arrange
        Sku sku = criarSkuValido();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sku.atualizarEstoque(-10));
    }

    // ========================================
    // TESTES DE ATIVAÇÃO/INATIVAÇÃO
    // ========================================

    @Test
    @DisplayName("Deve inativar SKU ativo com sucesso")
    void deveInativarSkuComSucesso() {
        // Arrange
        Sku sku = criarSkuValido();
        assertTrue(sku.isAtivo());

        // Act
        sku.inativar();

        // Assert
        assertFalse(sku.isAtivo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar inativar SKU já inativo")
    void deveLancarExcecaoAoInativarSkuJaInativo() {
        // Arrange
        Sku sku = criarSkuValido();
        sku.inativar(); // Inativa uma vez

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> sku.inativar() // Tenta inativar de novo
        );

        assertTrue(exception.getMessage().contains("já está inativo"));
    }
}
