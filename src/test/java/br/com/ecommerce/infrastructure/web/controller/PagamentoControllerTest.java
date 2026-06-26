package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.repository.PagamentoRepository;
import br.com.ecommerce.application.repository.PedidoRepository;
import br.com.ecommerce.domain.*;
import br.com.ecommerce.infrastructure.security.JwtAuthenticationFilter;
import br.com.ecommerce.infrastructure.security.JwtTokenProvider;
import br.com.ecommerce.infrastructure.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = PagamentoController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
class PagamentoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private PagamentoRepository pagamentoRepository;
    
    @MockitoBean
    private PedidoRepository pedidoRepository;
    
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("Deve criar pagamento para pedido existente")
    void deveCriarPagamento() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        Pedido pedido = new Pedido("Cliente Teste");
        ItemPedido item = new ItemPedido(UUID.randomUUID(), "Produto", new BigDecimal("100.00"), 2);
        pedido.adicionarItem(item);
        
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        doNothing().when(pagamentoRepository).salvar(any(Pagamento.class));
        
        String requestBody = """
            {
                "pedidoId": "%s",
                "formaPagamento": "PIX"
            }
            """.formatted(pedidoId);
        
        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Pagamento criado com sucesso")));
        
        verify(pagamentoRepository, times(1)).salvar(any(Pagamento.class));
    }
    
    @Test
    @DisplayName("Deve retornar 404 quando pedido não existe")
    void deveRetornar404QuandoPedidoNaoExiste() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());
        
        String requestBody = """
            {
                "pedidoId": "%s",
                "formaPagamento": "PIX"
            }
            """.formatted(pedidoId);
        
        mockMvc.perform(post("/pagamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Deve aprovar pagamento e atualizar pedido")
    void deveAprovarPagamento() throws Exception {
        UUID pagamentoId = UUID.randomUUID();
        UUID pedidoId = UUID.randomUUID();
        
        Pagamento pagamento = new Pagamento(pedidoId, new BigDecimal("200.00"), FormaPagamento.PIX);
        when(pagamentoRepository.buscarPorId(pagamentoId)).thenReturn(Optional.of(pagamento));
        doNothing().when(pagamentoRepository).salvar(any(Pagamento.class));
        
        Pedido pedido = new Pedido("Cliente");
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoRepository).save(any(Pedido.class));
        
        mockMvc.perform(post("/pagamentos/" + pagamentoId + "/aprovar"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Pagamento aprovado")));
        
        verify(pagamentoRepository, times(1)).salvar(any(Pagamento.class));
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }
    
    @Test
    @DisplayName("Deve listar todos os pagamentos")
    void deveListarPagamentos() throws Exception {
        UUID pedidoId = UUID.randomUUID();
        Pagamento pagamento = new Pagamento(pedidoId, new BigDecimal("100.00"), FormaPagamento.PIX);
        when(pagamentoRepository.listarTodos()).thenReturn(List.of(pagamento));
        
        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].valor").value(100.00))
                .andExpect(jsonPath("$[0].formaPagamento").value("PIX"));
    }
}
