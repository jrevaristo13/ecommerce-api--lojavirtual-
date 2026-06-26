package br.com.ecommerce.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cupons")
public class CupomEntity {
    
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_desconto", nullable = false)
    private TipoDesconto tipoDesconto;
    
    @Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;
    
    @Column(name = "valor_minimo_pedido", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMinimoPedido;
    
    @Column(name = "limite_uso", nullable = false)
    private Integer limiteUso;
    
    @Column(name = "quantidade_utilizada", nullable = false)
    private Integer quantidadeUtilizada;
    
    @Column(nullable = false)
    private Boolean ativo;
    
    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;
    
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    
    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;
    
    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;
    
    public enum TipoDesconto {
        PERCENTUAL, VALOR_FIXO
    }
    
    public CupomEntity() {}
    
    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public TipoDesconto getTipoDesconto() { return tipoDesconto; }
    public void setTipoDesconto(TipoDesconto tipoDesconto) { this.tipoDesconto = tipoDesconto; }
    
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }
    
    public BigDecimal getValorMinimoPedido() { return valorMinimoPedido; }
    public void setValorMinimoPedido(BigDecimal valorMinimoPedido) { this.valorMinimoPedido = valorMinimoPedido; }
    
    public Integer getLimiteUso() { return limiteUso; }
    public void setLimiteUso(Integer limiteUso) { this.limiteUso = limiteUso; }
    
    public Integer getQuantidadeUtilizada() { return quantidadeUtilizada; }
    public void setQuantidadeUtilizada(Integer quantidadeUtilizada) { this.quantidadeUtilizada = quantidadeUtilizada; }
    
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
    
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    
    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }
    
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
}
