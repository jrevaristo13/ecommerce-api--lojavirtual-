package br.com.ecommerce.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;
    
    public RefreshTokenEntity() {}
    
    public RefreshTokenEntity(String token, UUID usuarioId, Instant expiryDate) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.expiryDate = expiryDate;
    }
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    
    public Instant getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Instant expiryDate) { this.expiryDate = expiryDate; }
}
