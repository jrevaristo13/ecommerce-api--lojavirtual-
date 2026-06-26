package br.com.ecommerce.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "marcas")
public class MarcaEntity {
    
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    
    @Column(nullable = false, unique = true, length = 150)
    private String nome;
    
    @Column(nullable = false, length = 500)
    private String descricao;
    
    @Column(name = "site_oficial", nullable = false)
    private String siteOficial;
    
    @Column(name = "email_contato", nullable = false)
    private String emailContato;
    
    @Column(name = "telefone_contato", nullable = false, length = 20)
    private String telephoneContato;
    
    @Column(name = "logo_url", nullable = false)
    private String logoUrl;
    
    @Column(nullable = false)
    private boolean ativa = true;
    
    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    public MarcaEntity() {}
    
    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public String getSiteOficial() { return siteOficial; }
    public void setSiteOficial(String siteOficial) { this.siteOficial = siteOficial; }
    
    public String getEmailContato() { return emailContato; }
    public void setEmailContato(String emailContato) { this.emailContato = emailContato; }
    
    public String getTelephoneContato() { return telephoneContato; }
    public void setTelephoneContato(String telephoneContato) { this.telephoneContato = telephoneContato; }
    
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    
    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }
    
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
