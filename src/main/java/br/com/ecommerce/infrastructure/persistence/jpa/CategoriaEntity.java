package br.com.ecommerce.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "categorias")
public class CategoriaEntity {
    
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    
    @Column(nullable = false, length = 50)
    private String nome;
    
    @Column(nullable = false, length = 4000)
    private String descricao;
    
    @Column(name = "categoria_pai_id")
    private UUID categoriaPaiId;
    
    @Column(nullable = false)
    private boolean ativa = true;
    
    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;
    
    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
    
    public CategoriaEntity() {}
    
    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public UUID getCategoriaPaiId() { return categoriaPaiId; }
    public void setCategoriaPaiId(UUID categoriaPaiId) { this.categoriaPaiId = categoriaPaiId; }
    
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    
    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
}
