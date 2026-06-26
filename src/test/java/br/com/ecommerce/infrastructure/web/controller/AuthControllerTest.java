package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.dto.LoginRequest;
import br.com.ecommerce.application.dto.LoginResponse;
import br.com.ecommerce.application.usecase.AuthService;
import br.com.ecommerce.domain.Usuario;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = AuthController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private AuthService authService;
    
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    @DisplayName("Deve registrar novo usuário com sucesso")
    void deveRegistrarNovoUsuario() throws Exception {
        Usuario usuario = new Usuario("teste@example.com", "teste", "senha123", true);
        when(authService.register(any(), any(), any())).thenReturn(usuario);
        
        String requestBody = """
            {
                "email": "teste@example.com",
                "login": "teste",
                "senha": "senha123"
            }
            """;
        
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuário registrado com sucesso")));
    }
    
    @Test
    @DisplayName("Deve fazer login e retornar tokens JWT")
    void deveFazerLoginERetornarTokens() throws Exception {
        LoginResponse response = new LoginResponse("access-token-123", "refresh-token-456");
        when(authService.login(any(LoginRequest.class))).thenReturn(response);
        
        String requestBody = """
            {
                "email": "teste@example.com",
                "senha": "senha123"
            }
            """;
        
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-456"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }
    
    @Test
    @DisplayName("Deve renovar token com refresh token válido")
    void deveRenovarToken() throws Exception {
        LoginResponse response = new LoginResponse("novo-access-token", "novo-refresh-token");
        when(authService.refreshToken(any())).thenReturn(response);
        
        String requestBody = """
            {
                "refreshToken": "refresh-token-valido"
            }
            """;
        
        mockMvc.perform(post("/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("novo-access-token"));
    }
}
