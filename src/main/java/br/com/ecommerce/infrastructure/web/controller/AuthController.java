package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.dto.LoginRequest;
import br.com.ecommerce.application.dto.LoginResponse;
import br.com.ecommerce.application.dto.RefreshTokenRequest;
import br.com.ecommerce.application.usecase.AuthService;
import br.com.ecommerce.domain.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e autorização")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário e retorna tokens JWT")
    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário no sistema")
    @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        Usuario usuario = authService.register(request.email(), request.login(), request.senha());
        return ResponseEntity.ok("Usuário registrado com sucesso! ID: " + usuario.getId());
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Renova o access token usando um refresh token válido")
    @ApiResponse(responseCode = "200", description = "Token renovado com sucesso")
    @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }
    
    public record RegisterRequest(String email, String login, String senha) {}
}
