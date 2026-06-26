package br.com.ecommerce.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Pagamento {

    // ==========================================
    //       CONSTANTES DE REGRA DE NEGÓCIO
    // ==========================================
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;
    private static final BigDecimal VALOR_MINIMO = BigDecimal.ZERO;
    private static final BigDecimal VALOR_MAXIMO = new BigDecimal("1000000.00");
    private static final int TRANSACAOGATEWAYID_MINIMO = 3;
    private static final int TRANSACAOGATEWAYID_MAXIMO = 50;

    // ==========================================
    //        IDENTIDADE E ESTADO
    // ==========================================
    private final UUID id;
    private final UUID pedidoId; // Obrigatório para saber qual pedido está sendo pago
    private BigDecimal valor;
    private final FormaPagamento formaPagamento;
    private StatusPagamento status;
    private String transacaoGatewayId;  
    private final LocalDateTime dataCriacao;

    // ========================================== 
    //       CONSTRUTOR PRINCIPAL (CRIAÇÃO)
    // ==========================================
    public Pagamento(UUID pedidoId, BigDecimal valor, FormaPagamento formaPagamento) {
        validarId(pedidoId, "O ID do pedido não pode ser nulo.");
        validarValor(valor);
        Objects.requireNonNull(formaPagamento, "A forma de pagamento não pode ser nula.");

        this.id = UUID.randomUUID();
        this.pedidoId = pedidoId;
        this.valor = valor.setScale(2, ARREDONDAMENTO);
        this.formaPagamento = formaPagamento;
        
        // Todo pagamento novo nasce pendente e sem ID do gateway ainda
        this.status = StatusPagamento.PENDENTE;
        this.transacaoGatewayId = null; 
        this.dataCriacao = LocalDateTime.now();
    }

    // ========================================== 
    //       CONSTRUTOR DE RECONSTITUIÇÃO 
    // ==========================================
    public Pagamento(UUID id, UUID pedidoId, BigDecimal valor, FormaPagamento formaPagamento, 
                     StatusPagamento status, String transacaoGatewayId, LocalDateTime dataCriacao) {
        
        validarId(id, "O ID do pagamento não pode ser nulo.");
        validarId(pedidoId, "O ID do pedido não pode ser nulo.");
        validarValor(valor);
        Objects.requireNonNull(formaPagamento, "A forma de pagamento não pode ser nula.");
        Objects.requireNonNull(status, "O status do pagamento não pode ser nulo.");

        this.id = id;
        this.pedidoId = pedidoId;
        this.valor = valor.setScale(2, ARREDONDAMENTO);
        this.formaPagamento = formaPagamento;
        this.status = status;
        
        if (transacaoGatewayId != null) {
            String idTratado = normalizarTexto(transacaoGatewayId);
            validarTransacaoGatewayId(idTratado);
            this.transacaoGatewayId = idTratado;
        }

        this.dataCriacao = Objects.requireNonNull(dataCriacao, "A data de criação não pode ser nula.");
    }

    // ==========================================
    //        COMPORTAMENTOS DE NEGÓCIO
    // ==========================================

    public void registrarGatewayTransacao(String codigoTransacao) {
        String codigoTratado = normalizarTexto(codigoTransacao);
        validarTransacaoGatewayId(codigoTratado);
        this.transacaoGatewayId = codigoTratado;
    }

    public void aprovar() {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos PENDENTES podem ser aprovados.");
        }
        this.status = StatusPagamento.APROVADO;
    }

    public void recusar() {
        if (this.status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Apenas pagamentos PENDENTES podem ser recusados.");
        }
        this.status = StatusPagamento.RECUSADO;
    }

    public void estornar() {
        if (this.status != StatusPagamento.APROVADO) {
            throw new IllegalStateException("Apenas pagamentos APROVADOS podem ser estornados.");
        }
        this.status = StatusPagamento.ESTORNADO;
    }

    // ==========================================
    //       SANITIZAÇÃO
    // ==========================================
    private String normalizarTexto(String texto) {
        Objects.requireNonNull(texto, "O texto de entrada não pode ser nulo.");
        return texto.replaceAll("\\s+", " ").trim();
    }

    // ==========================================
    //             VALIDAÇÕES PRIVADAS
    // ==========================================
    private void validarId(UUID id, String mensagemErro) {
        if (id == null) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    private void validarValor(BigDecimal valorInput) {
        if (valorInput == null) {
            throw new IllegalArgumentException("O valor do pagamento não pode ser nulo.");
        }
        if (valorInput.compareTo(VALOR_MINIMO) <= 0) {
            throw new IllegalArgumentException("O valor do pagamento deve ser maior que zero.");
        }
        if (valorInput.compareTo(VALOR_MAXIMO) > 0) {
            throw new IllegalArgumentException("O valor do pagamento excede o limite máximo permitido.");
        }
    }

    private void validarTransacaoGatewayId(String codigo) {
        if (codigo.isBlank()) {
            throw new IllegalArgumentException("O ID da transação do gateway não pode ser vazio.");
        }
        if (codigo.length() < TRANSACAOGATEWAYID_MINIMO || codigo.length() > TRANSACAOGATEWAYID_MAXIMO) {
            throw new IllegalArgumentException(
                "O ID da transação deve ter entre " + TRANSACAOGATEWAYID_MINIMO + " e " + TRANSACAOGATEWAYID_MAXIMO + " caracteres."
            );
        }
    }

    // ==========================================
    //                 GETTERS
    // ==========================================
    public UUID getId() { return id; }
    public UUID getPedidoId() { return pedidoId; }
    public BigDecimal getValor() { return valor; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public StatusPagamento getStatus() { return status; }
    public String getTransacaoGatewayId() { return transacaoGatewayId; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }

    // ==========================================
    //             OBJECT METHODS
    // ==========================================

    @Override
    public boolean equals(Object obj) {
        // 1. Verifica se é a mesma referência na memória
        if (this == obj) return true;
        
        // 2. Garante a proteção contra nulos e tipos diferentes (seguro com proxies do Hibernate/JPA)
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // 3. Faz o cast seguro
        Pagamento other = (Pagamento) obj;
        
        // 4. Se um dos IDs for nulo, entidades transientes (não salvas) não são consideradas iguais
        if (this.id == null || other.id == null) return false;
        
        // 5. A identidade real da entidade é definida estritamente pelo ID único
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        // Alinhado com o equals, geramos o hash usando apenas o ID
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Pagamento{" +
                "id=" + id +
                ", pedidoId=" + pedidoId +
                ", valor=" + valor +
                ", formaPagamento=" + formaPagamento +
                ", status=" + status +
                ", transacaoGatewayId='" + (transacaoGatewayId != null ? transacaoGatewayId : "N/A") + '\'' +
                '}';
    }
}
