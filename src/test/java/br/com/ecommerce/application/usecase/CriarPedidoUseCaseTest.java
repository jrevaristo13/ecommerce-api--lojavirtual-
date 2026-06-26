package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.dto.ItemCompraDTO;
import br.com.ecommerce.application.repository.PedidoRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CriarPedidoUseCaseTest {

    @Mock
    private SkuRepository skuRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    private CriarPedidoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CriarPedidoUseCase(skuRepository, pedidoRepository);
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
    @DisplayName("Deve criar pedido com sucesso quando SKU tem estoque")
    void deveCriarPedidoComSucesso() {
        // Arrange
        Sku sku = criarSkuValido(10);
        ItemCompraDTO item = new ItemCompraDTO(sku.getId(), 2);
        when(skuRepository.findById(sku.getId())).thenReturn(Optional.of(sku));
        when(skuRepository.save(any(Sku.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(pedidoRepository).save(any());

        // Act
        useCase.executar("Cliente Teste", List.of(item));

        // Assert
        verify(skuRepository).findById(sku.getId());
        verify(skuRepository).save(any(Sku.class));
        verify(pedidoRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de itens é vazia")
    void deveLancarExcecaoQuandoListaVazia() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> useCase.executar("Cliente", List.of()));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando lista de itens é nula")
    void deveLancarExcecaoQuandoListaNula() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> useCase.executar("Cliente", null));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando SKU não existe")
    void deveLancarExcecaoQuandoSkuNaoExiste() {
        // Arrange
        UUID skuId = UUID.randomUUID();
        ItemCompraDTO item = new ItemCompraDTO(skuId, 2);
        when(skuRepository.findById(skuId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> useCase.executar("Cliente", List.of(item)));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve debitar estoque ao criar pedido")
    void deveDebitarEstoqueAoCriarPedido() {
        // Arrange
        Sku sku = criarSkuValido(10);
        int estoqueAntes = sku.getQuantidadeEstoque();
        ItemCompraDTO item = new ItemCompraDTO(sku.getId(), 3);
        when(skuRepository.findById(sku.getId())).thenReturn(Optional.of(sku));
        when(skuRepository.save(any(Sku.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(pedidoRepository).save(any());

        // Act
        useCase.executar("Cliente", List.of(item));

        // Assert
        assertEquals(estoqueAntes - 3, sku.getQuantidadeEstoque());
        verify(skuRepository).save(sku);
    }
}
