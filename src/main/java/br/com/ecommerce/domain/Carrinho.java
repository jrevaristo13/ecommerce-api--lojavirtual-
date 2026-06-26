package br.com.ecommerce.domain;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Carrinho {

    // ==========================================
    //                 ENUMS
    // ==========================================
    public enum StatusCarrinho {
        ABERTO,
        FINALIZADO,
        ABANDONADO
    }

    // ==========================================
    //       CONSTANTES DE REGRA DE NEGÓCIO
    // ==========================================
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;
    private static final BigDecimal LIMITE_MAXIMO_CARRINHO = new BigDecimal("5000000.00");

    // ==========================================
    //        IDENTIDADE E ESTADO
    // ==========================================
    private final UUID id;
    private final UUID clienteId;
    private final List<ItemCarrinho> itens; // Entidade interna (ou Value Object) do carrinho
    private Cupom cupomAplicado;
    private StatusCarrinho status;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    // ========================================== 
    //       CONSTRUTOR PRINCIPAL (CRIAÇÃO)
    // ==========================================
    public Carrinho(UUID clienteId) {
        this.id = UUID.randomUUID();
        this.clienteId = Objects.requireNonNull(clienteId, "O ID do cliente não pode ser nulo.");
        this.itens = new ArrayList<>();
        this.status = StatusCarrinho.ABERTO;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = this.dataCriacao;
    }

    // ========================================== 
    //       CONSTRUTOR DE RECONSTITUIÇÃO
    // ==========================================
    public Carrinho(UUID id, UUID clienteId, List<ItemCarrinho> itens, Cupom cupomAplicado, 
                    StatusCarrinho status, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.clienteId = clienteId;
        this.itens = new ArrayList<>(itens);
        this.cupomAplicado = cupomAplicado;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // ==========================================
    //        COMPORTAMENTOS DE NEGÓCIO
    // ==========================================

    public void adicionarItem(ItemCarrinho novoItem) {
        assegurarCarrinhoAberto();
        Objects.requireNonNull(novoItem, "O item do carrinho não pode ser nulo.");
        
        // Verifica se o SKU/Produto já existe no carrinho
        ItemCarrinho itemExistente = buscarItemPorProduto(novoItem.getProdutoId());
        
        if (itemExistente != null) {
            // Se já existe, incrementa a quantidade atual com a nova quantidade
            itemExistente.alterarQuantidade(itemExistente.getQuantidade() + novoItem.getQuantidade());
        } else {
            // Se for novo, adiciona o objeto diretamente na lista
            this.itens.add(novoItem);
        }

        validarLimiteMaximoCarrinho();
        atualizarDataAtualizacao();
    }

    public void adicionarItem(UUID produtoId, String nomeProduto, int quantidade, BigDecimal precoUnitario) {
        assegurarCarrinhoAberto();
        
        // Verifica se o produto já existe no carrinho para apenas incrementar a quantidade
        ItemCarrinho itemExistente = buscarItemPorProduto(produtoId);
        if (itemExistente != null) {
            itemExistente.alterarQuantidade(itemExistente.getQuantidade() + quantidade);
        } else {
            this.itens.add(new ItemCarrinho(produtoId, nomeProduto, quantidade, precoUnitario));
        }

        validarLimiteMaximoCarrinho();
        atualizarDataAtualizacao();
    }

    public void removerItem(UUID produtoId) {
        assegurarCarrinhoAberto();
        ItemValidacao: {
            ItemCarrinho item = buscarItemPorProduto(produtoId);
            if (item == null) {
                throw new IllegalArgumentException("Item não encontrado no carrinho.");
            }
            this.itens.remove(item);
        }
        
        // Se removeu itens e o cupom perdeu a validade por valor mínimo, removemos o cupom
        checarValidadeCupomAtual();
        atualizarDataAtualizacao();
    }

    public void atualizarQuantidadeItem(UUID produtoId, int novaQuantidade) {
        assegurarCarrinhoAberto();
        ItemCarrinho item = buscarItemPorProduto(produtoId);
        if (item == null) {
            throw new IllegalArgumentException("Item não encontrado no carrinho.");
        }
        
        item.alterarQuantidade(novaQuantidade);
        validarLimiteMaximoCarrinho();
        checarValidadeCupomAtual();
        atualizarDataAtualizacao();
    }

    public void aplicarCupom(Cupom cupom) {
        assegurarCarrinhoAberto();
        Objects.requireNonNull(cupom, "O cupom não pode ser nulo.");

        if (!cupom.podeSerUtilizado()) {
            throw new IllegalArgumentException("Este cupom não está ativo ou já expirou.");
        }

        if (calcularValorSubtotal().compareTo(cupom.getValorMinimoPedido()) < 0) {
            throw new IllegalArgumentException("O valor subtotal do carrinho é menor que o valor mínimo exigido pelo cupom (R$ " + cupom.getValorMinimoPedido() + ").");
        }

        this.cupomAplicado = cupom;
        atualizarDataAtualizacao();
    }

    public void removerCupom() {
        assegurarCarrinhoAberto();
        this.cupomAplicado = null;
        atualizarDataAtualizacao();
    }

    public void finalizarCarrinho() {
        assegurarCarrinhoAberto();
        if (this.itens.isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar um carrinho vazio.");
        }
        
        // Se houver cupom, consolida a utilização dele no momento do fechamento
        if (this.cupomAplicado != null) {
            this.cupomAplicado.registrarUtilizacao();
        }
        
        this.status = StatusCarrinho.FINALIZADO;
        atualizarDataAtualizacao();
    }

    public void abandonarCarrinho() {
        assegurarCarrinhoAberto();
        this.status = StatusCarrinho.ABANDONADO;
        atualizarDataAtualizacao();
    }

    // ==========================================
    //            MÉTODOS DE CÁLCULO
    // ==========================================

    public BigDecimal calcularValorSubtotal() {
        return this.itens.stream()
                .map(ItemCarrinho::calcularValorTotalItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, ARREDONDAMENTO);
    }

    public BigDecimal calcularValorDesconto() {
        if (this.cupomAplicado == null) {
            return BigDecimal.ZERO.setScale(2, ARREDONDAMENTO);
        }
        return this.cupomAplicado.calcularDesconto(calcularValorSubtotal());
    }

    public BigDecimal calcularValorTotalGeral() {
        BigDecimal subtotal = calcularValorSubtotal();
        BigDecimal desconto = calcularValorDesconto();
        BigDecimal total = subtotal.subtract(desconto);
        
        return total.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO.setScale(2, ARREDONDAMENTO) : total.setScale(2, ARREDONDAMENTO);
    }

    // ==========================================
    //          VALIDAÇÕES E AUXILIARES
    // ==========================================

    private void assegurarCarrinhoAberto() {
        if (this.status != StatusCarrinho.ABERTO) {
            throw new IllegalStateException("Operação não permitida. O carrinho não está mais aberto.");
        }
    }

    private void validarLimiteMaximoCarrinho() {
        if (calcularValorSubtotal().compareTo(LIMITE_MAXIMO_CARRINHO) > 0) {
            throw new IllegalArgumentException("O valor total do carrinho excede o limite máximo permitido por compra.");
        }
    }

    private void checarValidadeCupomAtual() {
        if (this.cupomAplicado != null && calcularValorSubtotal().compareTo(this.cupomAplicado.getValorMinimoPedido()) < 0) {
            this.cupomAplicado = null; // Remove o cupom automaticamente se o valor cair abaixo do mínimo
        }
    }

    private ItemCarrinho buscarItemPorProduto(UUID produtoId) {
        return this.itens.stream()
                .filter(item -> item.getProdutoId().equals(produtoId))
                .findFirst()
                .orElse(null);
    }

    private void atualizarDataAtualizacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // ==========================================
    //                 GETTERS
    // ==========================================
    public UUID getId() { return id; }
    public UUID getClienteId() { return clienteId; }
    public List<ItemCarrinho> getItens() { return Collections.unmodifiableList(itens); } // Protege a lista contra manipulação externa
    public Cupom getCupomAplicado() { return cupomAplicado; }
    public StatusCarrinho getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    @Override
    public String toString() {
        return "Carrinho{" + "id=" + id + ", status=" + status + ", subtotal=" + calcularValorSubtotal() + ", total=" + calcularValorTotalGeral() + '}';
    }
}
