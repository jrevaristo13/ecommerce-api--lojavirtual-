package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.usecase.CriarPedidoUseCase;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = PedidoController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private CriarPedidoUseCase criarPedidoUseCase;
    
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("Deve criar pedido com sucesso")
    void deveCriarPedido() throws Exception {
        doNothing().when(criarPedidoUseCase).executar(any(), anyList());
        
        String requestBody = """
            {
                "cliente": "Cliente Teste",
                "itens": [
                    {
                        "skuId": "%s",
                        "quantidade": 2
                    }
                ]
            }
            """.formatted(UUID.randomUUID());
        
        mockMvc.perform(post("/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Pedido criado com sucesso!"));
    }
    
    @Test
    @DisplayName("Deve retornar erro quando itens estão vazios")
    void deveRetornarErroQuandoItensVazios() throws Exception {
        doThrow(new IllegalArgumentException("O pedido deve conter pelo menos um item."))
            .when(criarPedidoUseCase).executar(any(), anyList());
        
        String requestBody = """
            {
                "cliente": "Cliente Teste",
                "itens": []
            }
            """;
        
        mockMvc.perform(post("/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
}
