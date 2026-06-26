package br.com.ecommerce.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carrinhos")
public class CarrinhoEntity {
    
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    
    @Column(name = "cliente_id", nullable = false, unique = true)
    private UUID clienteId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCarrinho status;
    
    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrinhoEntity> itens = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupom_id")
    private CupomEntity cupomAplicado;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    public enum StatusCarrinho {
        ABERTO, FINALIZADO, ABANDONADO
    }
    
    public CarrinhoEntity() {}
    
    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    
    public StatusCarrinho getStatus() { return status; }
    public void setStatus(StatusCarrinho status) { this.status = status; }
    
    public List<ItemCarrinhoEntity> getItens() { return itens; }
    public void setItens(List<ItemCarrinhoEntity> itens) { this.itens = itens; }
    
    public CupomEntity getCupomAplicado() { return cupomAplicado; }
    public void setCupomAplicado(CupomEntity cupomAplicado) { this.cupomAplicado = cupomAplicado; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
