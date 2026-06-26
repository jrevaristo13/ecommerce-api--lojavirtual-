package br.com.ecommerce.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    private Pedido pedido;

    @BeforeEach
    void setUp() {
        pedido = new Pedido("Cliente Teste");
    }

    @Nested
    @DisplayName("Criar pedido")
    class CriarPedidoTest {

        @Test
        @DisplayName("Deve criar pedido com cliente")
        void deveCriarPedidoComCliente() {
            assertNotNull(pedido.getId());
            assertEquals("Cliente Teste", pedido.getCliente());
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente é nulo")
        void deveLancarExcecaoQuandoClienteNulo() {
            assertThrows(NullPointerException.class, () -> new Pedido(null));
        }

        @Test
        @DisplayName("Deve criar pedido vazio inicialmente")
        void deveCriarPedidoVazioInicialmente() {
            assertTrue(pedido.getItens().isEmpty());
        }
    }

    @Nested
    @DisplayName("Adicionar itens")
    class AdicionarItensTest {

        @Test
        @DisplayName("Deve adicionar item ao pedido")
        void deveAdicionarItemAoPedido() {
            ItemPedido item = new ItemPedido(
                UUID.randomUUID(),
                "Camiseta Premium",
                new BigDecimal("99.90"),
                2
            );
            
            pedido.adicionarItem(item);
            
            assertEquals(1, pedido.getItens().size());
        }

        @Test
        @DisplayName("Deve adicionar múltiplos itens")
        void deveAdicionarMultiplosItens() {
            ItemPedido item1 = new ItemPedido(UUID.randomUUID(), "Item 1", new BigDecimal("50.00"), 1);
            ItemPedido item2 = new ItemPedido(UUID.randomUUID(), "Item 2", new BigDecimal("100.00"), 2);
            
            pedido.adicionarItem(item1);
            pedido.adicionarItem(item2);
            
            assertEquals(2, pedido.getItens().size());
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar item nulo")
        void deveLancarExcecaoAoAdicionarItemNulo() {
            assertThrows(NullPointerException.class, () -> pedido.adicionarItem(null));
        }
    }
}
