package br.com.ecommerce.application.usecase;

import br.com.ecommerce.application.dto.LoginRequest;
import br.com.ecommerce.application.dto.LoginResponse;
import br.com.ecommerce.application.repository.UsuarioRepository;
import br.com.ecommerce.domain.Usuario;
import br.com.ecommerce.infrastructure.persistence.jpa.RefreshTokenEntity;
import br.com.ecommerce.infrastructure.persistence.jpa.RefreshTokenRepository;
import br.com.ecommerce.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    
    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider, RefreshTokenRepository refreshTokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }
    
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorEmail(request.email());
        
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Email ou senha inválidos");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos");
        }
        
        usuario.garantirQueEstaAtivo();
        
        String accessToken = tokenProvider.generateToken(usuario.getId(), usuario.getEmail());
        String refreshToken = createRefreshToken(usuario.getId());
        
        return new LoginResponse(accessToken, refreshToken);
    }
    
    @Transactional
    public String createRefreshToken(UUID usuarioId) {
        refreshTokenRepository.deleteByUsuarioId(usuarioId);
        
        String token = UUID.randomUUID().toString();
        Instant expiryDate = Instant.now().plusMillis(tokenProvider.getRefreshExpiration());
        
        RefreshTokenEntity refreshToken = new RefreshTokenEntity(token, usuarioId, expiryDate);
        refreshTokenRepository.save(refreshToken);
        
        return token;
    }
    
    @Transactional
    public LoginResponse refreshToken(String refreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));
        
        if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(tokenEntity);
            throw new RuntimeException("Refresh token expirado");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(tokenEntity.getUsuarioId());
        
        if (usuarioOpt.isEmpty() || !usuarioOpt.get().isStatusAtivo()) {
            throw new RuntimeException("Usuário inválido ou inativo");
        }
        
        Usuario usuario = usuarioOpt.get();
        String newAccessToken = tokenProvider.generateToken(usuario.getId(), usuario.getEmail());
        String newRefreshToken = createRefreshToken(usuario.getId());
        
        return new LoginResponse(newAccessToken, newRefreshToken);
    }
    
    @Transactional
    public Usuario register(String email, String login, String senha) {
        if (usuarioRepository.buscarPorEmail(email).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        
        if (usuarioRepository.buscarPorLogin(login).isPresent()) {
            throw new RuntimeException("Login já cadastrado");
        }
        
        String senhaCriptografada = passwordEncoder.encode(senha);
        Usuario usuario = new Usuario(email, login, senhaCriptografada, true);
        
        usuarioRepository.salvar(usuario);
        return usuario;
    }
}
