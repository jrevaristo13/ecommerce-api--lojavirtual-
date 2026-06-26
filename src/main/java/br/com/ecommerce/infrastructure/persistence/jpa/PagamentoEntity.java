package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.FormaPagamento;
import br.com.ecommerce.domain.StatusPagamento;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagamentos")
public class PagamentoEntity {
    
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    
    @Column(name = "pedido_id", nullable = false)
    private UUID pedidoId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;
    
    @Column(name = "transacao_gateway_id", length = 50)
    private String transacaoGatewayId;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    public PagamentoEntity() {}
    
    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getPedidoId() { return pedidoId; }
    public void setPedidoId(UUID pedidoId) { this.pedidoId = pedidoId; }
    
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(FormaPagamento formaPagamento) { this.formaPagamento = formaPagamento; }
    
    public StatusPagamento getStatus() { return status; }
    public void setStatus(StatusPagamento status) { this.status = status; }
    
    public String getTransacaoGatewayId() { return transacaoGatewayId; }
    public void setTransacaoGatewayId(String transacaoGatewayId) { this.transacaoGatewayId = transacaoGatewayId; }
    
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
