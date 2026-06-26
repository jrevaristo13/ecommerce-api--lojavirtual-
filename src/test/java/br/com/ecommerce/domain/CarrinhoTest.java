package br.com.ecommerce.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarrinhoTest {

    private Carrinho carrinho;
    private UUID clienteId;
    private UUID produtoId;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        produtoId = UUID.randomUUID();
        carrinho = new Carrinho(clienteId);
    }

    @Nested
    @DisplayName("Criação do Carrinho")
    class CriacaoTest {

        @Test
        @DisplayName("Deve criar carrinho vazio com status ABERTO")
        void deveCriarCarrinhoVazioComStatusAberto() {
            assertTrue(carrinho.getItens().isEmpty());
            assertEquals(Carrinho.StatusCarrinho.ABERTO, carrinho.getStatus());
            assertEquals(clienteId, carrinho.getClienteId());
            assertNotNull(carrinho.getId());
            assertNotNull(carrinho.getDataCriacao());
        }

        @Test
        @DisplayName("Deve lançar exceção quando clienteId é nulo")
        void deveLancarExcecaoQuandoClienteIdNulo() {
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Carrinho(null)
            );
            assertEquals("O ID do cliente não pode ser nulo.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Adicionar Itens")
    class AdicionarItensTest {

        @Test
        @DisplayName("Deve adicionar item ao carrinho vazio")
        void deveAdicionarItemAoCarrinhoVazio() {
            carrinho.adicionarItem(produtoId, "Camiseta", 2, new BigDecimal("99.90"));
            
            assertEquals(1, carrinho.getItens().size());
            assertEquals(new BigDecimal("199.80"), carrinho.calcularValorSubtotal());
        }

        @Test
        @DisplayName("Deve incrementar quantidade quando produto já existe")
        void deveIncrementarQuantidadeQuandoProdutoJaExiste() {
            carrinho.adicionarItem(produtoId, "Camiseta", 2, new BigDecimal("50.00"));
            carrinho.adicionarItem(produtoId, "Camiseta", 3, new BigDecimal("50.00"));
            
            assertEquals(1, carrinho.getItens().size());
            assertEquals(5, carrinho.getItens().get(0).getQuantidade());
        }

        @Test
        @DisplayName("Deve adicionar múltiplos produtos diferentes")
        void deveAdicionarMultiplosProdutosDiferentes() {
            UUID produto1 = UUID.randomUUID();
            UUID produto2 = UUID.randomUUID();
            
            carrinho.adicionarItem(produto1, "Camiseta", 1, new BigDecimal("50.00"));
            carrinho.adicionarItem(produto2, "Calça", 2, new BigDecimal("100.00"));
            
            assertEquals(2, carrinho.getItens().size());
            assertEquals(new BigDecimal("250.00"), carrinho.calcularValorSubtotal());
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar item nulo")
        void deveLancarExcecaoAoAdicionarItemNulo() {
            assertThrows(NullPointerException.class, () -> 
                carrinho.adicionarItem((ItemCarrinho) null));
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar em carrinho finalizado")
        void deveLancarExcecaoAoAdicionarEmCarrinhoFinalizado() {
            carrinho.adicionarItem(produtoId, "Camiseta", 1, new BigDecimal("50.00"));
            carrinho.finalizarCarrinho();
            
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> carrinho.adicionarItem(produtoId, "Calça", 1, new BigDecimal("100.00"))
            );
            assertTrue(exception.getMessage().contains("não está mais aberto"));
        }
    }

    @Nested
    @DisplayName("Remover Itens")
    class RemoverItensTest {

        @BeforeEach
        void adicionarItem() {
            carrinho.adicionarItem(produtoId, "Camiseta", 2, new BigDecimal("50.00"));
        }

        @Test
        @DisplayName("Deve remover item existente")
        void deveRemoverItemExistente() {
            carrinho.removerItem(produtoId);
            assertTrue(carrinho.getItens().isEmpty());
        }

        @Test
        @DisplayName("Deve lançar exceção ao remover item inexistente")
        void deveLancarExcecaoAoRemoverItemInexistente() {
            UUID produtoInexistente = UUID.randomUUID();
            
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carrinho.removerItem(produtoInexistente)
            );
            assertEquals("Item não encontrado no carrinho.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cálculos Financeiros")
    class CalculosTest {

        @Test
        @DisplayName("Deve calcular subtotal corretamente")
        void deveCalcularSubtotalCorretamente() {
            carrinho.adicionarItem(produtoId, "Camiseta", 3, new BigDecimal("100.00"));
            assertEquals(new BigDecimal("300.00"), carrinho.calcularValorSubtotal());
        }

        @Test
        @DisplayName("Deve retornar zero quando carrinho está vazio")
        void deveRetornarZeroQuandoCarrinhoVazio() {
            assertEquals(BigDecimal.ZERO.setScale(2), carrinho.calcularValorSubtotal());
        }

        @Test
        @DisplayName("Deve calcular total geral sem cupom igual ao subtotal")
        void deveCalcularTotalGeralSemCupom() {
            carrinho.adicionarItem(produtoId, "Camiseta", 2, new BigDecimal("50.00"));
            assertEquals(new BigDecimal("100.00"), carrinho.calcularValorTotalGeral());
        }

        @Test
        @DisplayName("Deve retornar desconto zero quando não há cupom")
        void deveRetornarDescontoZeroSemCupom() {
            carrinho.adicionarItem(produtoId, "Camiseta", 1, new BigDecimal("100.00"));
            assertEquals(BigDecimal.ZERO.setScale(2), carrinho.calcularValorDesconto());
        }
    }

    @Nested
    @DisplayName("Finalizar Carrinho")
    class FinalizarTest {

        @Test
        @DisplayName("Deve finalizar carrinho com itens")
        void deveFinalizarCarrinhoComItens() {
            carrinho.adicionarItem(produtoId, "Camiseta", 1, new BigDecimal("50.00"));
            carrinho.finalizarCarrinho();
            assertEquals(Carrinho.StatusCarrinho.FINALIZADO, carrinho.getStatus());
        }

        @Test
        @DisplayName("Deve lançar exceção ao finalizar carrinho vazio")
        void deveLancarExcecaoAoFinalizarCarrinhoVazio() {
            IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> carrinho.finalizarCarrinho()
            );
            assertEquals("Não é possível finalizar um carrinho vazio.", exception.getMessage());
        }

        @Test
        @DisplayName("Não deve permitir adicionar itens após finalizar")
        void naoDevePermitirAdicionarAposFinalizar() {
            carrinho.adicionarItem(produtoId, "Camiseta", 1, new BigDecimal("50.00"));
            carrinho.finalizarCarrinho();
            assertThrows(IllegalStateException.class, () -> 
                carrinho.adicionarItem(produtoId, "Calça", 1, new BigDecimal("100.00")));
        }
    }

    @Nested
    @DisplayName("Abandonar Carrinho")
    class AbandonarTest {

        @Test
        @DisplayName("Deve abandonar carrinho com status correto")
        void deveAbandonarCarrinhoComStatusCorreto() {
            carrinho.abandonarCarrinho();
            assertEquals(Carrinho.StatusCarrinho.ABANDONADO, carrinho.getStatus());
        }

        @Test
        @DisplayName("Não deve permitir abandonar carrinho já finalizado")
        void naoDevePermitirAbandonarCarrinhoFinalizado() {
            carrinho.adicionarItem(produtoId, "Camiseta", 1, new BigDecimal("50.00"));
            carrinho.finalizarCarrinho();
            assertThrows(IllegalStateException.class, () -> carrinho.abandonarCarrinho());
        }
    }

    @Nested
    @DisplayName("Imutabilidade da Lista de Itens")
    class ImutabilidadeTest {

        @Test
        @DisplayName("Não deve permitir modificar lista de itens externamente")
        void naoDevePermitirModificarListaExternamente() {
            carrinho.adicionarItem(produtoId, "Camiseta", 1, new BigDecimal("50.00"));
            var itens = carrinho.getItens();
            assertThrows(UnsupportedOperationException.class, () -> itens.clear());
        }
    }

    @Nested
    @DisplayName("Limite Máximo do Carrinho")
    class LimiteMaximoTest {

        @Test
        @DisplayName("Deve lançar exceção quando excede limite máximo de R$ 5.000.000,00")
        void deveLancarExcecaoQuandoExcedeLimiteMaximo() {
            // ✅ CORREÇÃO: Envolver a chamada dentro do assertThrows
            assertThrows(IllegalArgumentException.class, () -> 
                carrinho.adicionarItem(produtoId, "Produto Caro", 1, new BigDecimal("5000001.00"))
            );
        }

        @Test
        @DisplayName("Deve aceitar valor exatamente no limite máximo")
        void deveAceitarValorNoLimiteMaximo() {
            // Não deve lançar exceção (valor está exatamente no limite)
            assertDoesNotThrow(() -> 
                carrinho.adicionarItem(produtoId, "Produto Limite", 1, new BigDecimal("5000000.00"))
            );
        }
    }
}
