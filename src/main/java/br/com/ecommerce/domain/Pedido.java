package br.com.ecommerce.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Pedido {

    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;

    private final UUID id;
    private final String cliente;
    private final List<ItemPedido> itens;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private final LocalDateTime dataCriacao;

    // ========================================== 
    //       CONSTRUTOR PRINCIPAL (CRIAÇÃO)
    // ==========================================
    public Pedido(String cliente) {
        this.id = UUID.randomUUID();
        this.cliente = Objects.requireNonNull(cliente, "O cliente não pode ser nulo.");
        this.itens = new ArrayList<>();
        this.status = StatusPedido.AGUARDANDO_PAGAMENTO;
        this.valorTotal = BigDecimal.ZERO.setScale(2, ARREDONDAMENTO);
        this.dataCriacao = LocalDateTime.now();
    }

    // ==========================================
    // CONSTRUTOR DE RECONSTITUIÇÃO (Banco de Dados)
    // ==========================================
    public Pedido(UUID id, String cliente, List<ItemPedido> itens, StatusPedido status,
                  BigDecimal valorTotal, LocalDateTime dataCriacao) {
        this.id = Objects.requireNonNull(id, "O ID do pedido não pode ser nulo.");
        this.cliente = Objects.requireNonNull(cliente, "O cliente não pode ser nulo.");
        this.itens = new ArrayList<>(itens != null ? itens : Collections.emptyList());
        this.status = Objects.requireNonNull(status, "O status não pode ser nulo.");
        this.valorTotal = valorTotal != null ? valorTotal.setScale(2, ARREDONDAMENTO) : BigDecimal.ZERO.setScale(2, ARREDONDAMENTO);
        this.dataCriacao = Objects.requireNonNull(dataCriacao, "A data de criação não pode ser nula.");
    }

    // ==========================================
    //        COMPORTAMENTOS DE NEGÓCIO
    // ==========================================

    public void adicionarItem(ItemPedido novoItem) {
        Objects.requireNonNull(novoItem, "O item do pedido não pode ser nulo.");
        
        if (this.itens.contains(novoItem)) {
            ItemPedido existente = this.itens.get(this.itens.indexOf(novoItem));
            existente.adicionarQuantidade(novoItem.getQuantidade());
        } else {
            this.itens.add(novoItem);
        }
        
        recalcularValorTotal();
    }

    public void pagar() {
        if (this.status != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Apenas pedidos aguardando pagamento podem ser pagos.");
        }
        this.status = StatusPedido.PAGO;
    }

    public void cancelar() {
        if (this.status == StatusPedido.ENTREGUE) {
            throw new IllegalStateException("Não é possível cancelar um pedido que já foi entregue.");
        }
        this.status = StatusPedido.CANCELADO;
    }

    // ⭐ NOVO MÉTODO: Marcar como pago quando pagamento é aprovado
    public void marcarComoPago() {
        if (this.status != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("Apenas pedidos aguardando pagamento podem ser marcados como pagos.");
        }
        this.status = StatusPedido.PAGO;
    }

    private void recalcularValorTotal() {
        this.valorTotal = this.itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, ARREDONDAMENTO);
    }

    // ==========================================
    //                 GETTERS
    // ==========================================
    public UUID getId() { return id; }
    public String getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }
    public StatusPedido getStatus() { return status; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}