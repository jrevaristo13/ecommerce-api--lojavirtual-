package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.ItemPedido;
import br.com.ecommerce.domain.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PedidoPersistenciaTest {
    
    @Autowired
    private PedidoRepositoryJpaImpl pedidoRepository;
    
    @Test
    void deveSalvarPedidoNoBanco() {
        // Criar pedido
        Pedido pedido = new Pedido("Cliente Teste");
        ItemPedido item = new ItemPedido(UUID.randomUUID(), "Produto Teste", new BigDecimal("100.00"), 2);
        pedido.adicionarItem(item);
        
        System.out.println("=== ANTES DE SALVAR ===");
        System.out.println("Pedido ID: " + pedido.getId());
        System.out.println("Cliente: " + pedido.getCliente());
        System.out.println("Itens: " + pedido.getItens().size());
        System.out.println("Valor Total: " + pedido.getValorTotal());
        
        // Salvar
        pedidoRepository.save(pedido);
        
        System.out.println("=== DEPOIS DE SALVAR ===");
        
        // Buscar
        Optional<Pedido> encontrado = pedidoRepository.findById(pedido.getId());
        
        System.out.println("Encontrado: " + encontrado.isPresent());
        
        assertTrue(encontrado.isPresent(), "Pedido deve ser encontrado no banco");
        assertEquals(pedido.getId(), encontrado.get().getId());
        assertEquals("Cliente Teste", encontrado.get().getCliente());
    }
}
