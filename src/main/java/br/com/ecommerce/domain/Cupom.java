package br.com.ecommerce.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

import br.com.ecommerce.domain.TipoDescontos.TipoDesconto;

public class Cupom {
    // ==========================================
    //       CONSTANTES DE REGRA DE NEGÓCIO
    // ==========================================
    private static final int CODIGO_MINIMO = 5;
    private static final int CODIGO_MAXIMO = 20;
    private static final Pattern CODIGO_VALIDO = Pattern.compile("^[A-Z0-9]{5,20}$");

    private static final int DESCRICAO_MINIMO = 5;
    private static final int DESCRICAO_MAXIMO = 100;
    private static final Pattern DESCRICAO_VALIDA = Pattern.compile("^[\\p{L}0-9\\s.,()'%:-]+$");

    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;
    private static final BigDecimal VALORPEDIDO_MINIMO = BigDecimal.ZERO;
    private static final BigDecimal VALORPEDIDO_MAXIMO = new BigDecimal("1000000.00");
    
    private static final BigDecimal PERCENTUAL_MINIMO = new BigDecimal("0.01");
    private static final BigDecimal PERCENTUAL_MAXIMO = new BigDecimal("100.00");
    private static final BigDecimal DESCONTO_FIXO_MAXIMO = new BigDecimal("1000000.00");

    // ==========================================
    //        IDENTIDADE E ESTADO
    // ==========================================
    private final UUID id;
    private String codigo;
    private String descricao;
    private TipoDesconto tipoDesconto;
    private BigDecimal valorDesconto;
    private BigDecimal valorMinimoPedido;
    private int limiteUso;
    private int quantidadeUtilizada;
    private boolean ativo;
    private final LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    // ========================================== 
    //       CONSTRUTOR PRINCIPAL (CRIAÇÃO)
    // ==========================================
    public Cupom(String codigo, String descricao, TipoDesconto tipoDesconto, BigDecimal valorDesconto, 
                 BigDecimal valorMinimoPedido, int limiteUso, int quantidadeUtilizada, boolean ativo, 
                 LocalDateTime dataInicio, LocalDateTime dataFim) {

        this.id = UUID.randomUUID();

        Objects.requireNonNull(codigo, "O código do Cupom não pode ser nulo.");
        Objects.requireNonNull(descricao, "A descrição do Cupom não pode ser nula.");
        Objects.requireNonNull(tipoDesconto, "O tipo de desconto do Cupom não pode ser nulo.");
        Objects.requireNonNull(valorDesconto, "O valor de desconto do Cupom não pode ser nulo.");
        Objects.requireNonNull(valorMinimoPedido, "O valor mínimo de pedido do Cupom não pode ser nulo.");
        Objects.requireNonNull(dataInicio, "A data de início do Cupom não pode ser nula.");
        Objects.requireNonNull(dataFim, "A data fim do Cupom não pode ser nula.");

        String codigoTratado = normalizarTexto(codigo).toUpperCase();
        String descricaoTratada = normalizarTexto(descricao);

        this.dataCadastro = LocalDateTime.now();
        this.dataAtualizacao = this.dataCadastro;

        // Validando o estado inicial do Cupom
        validarCodigo(codigoTratado);
        validarDescricao(descricaoTratada);
        validarValorMinimoPedido(valorMinimoPedido);
        validarLimiteUso(limiteUso);
        validarPeriodo(dataInicio, dataFim);
        validarQuantidadeUtilizada(quantidadeUtilizada, limiteUso);
        validarDescontoEstrategia(tipoDesconto, valorDesconto, valorMinimoPedido);

        this.codigo = codigoTratado;
        this.descricao = descricaoTratada;
        this.tipoDesconto = tipoDesconto;
        this.valorDesconto = valorDesconto.setScale(2, ARREDONDAMENTO);
        this.valorMinimoPedido = valorMinimoPedido.setScale(2, ARREDONDAMENTO);
        this.limiteUso = limiteUso;
        this.quantidadeUtilizada = quantidadeUtilizada;
        this.ativo = ativo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    // ========================================== 
    //       CONSTRUTOR DE RECONSTITUIÇÃO
    // ==========================================
    public Cupom(UUID id, String codigo, String descricao, TipoDesconto tipoDesconto, BigDecimal valorDesconto,
                 BigDecimal valorMinimoPedido, int limiteUso, int quantidadeUtilizada, boolean ativo, 
                 LocalDateTime dataCadastro, LocalDateTime dataAtualizacao, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoDesconto = tipoDesconto;
        this.valorDesconto = valorDesconto;
        this.valorMinimoPedido = valorMinimoPedido;
        this.limiteUso = limiteUso;
        this.quantidadeUtilizada = quantidadeUtilizada;
        this.ativo = ativo;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    // ==========================================
    // VALIDAÇÕES PRIVADAS (GATES)
    // ==========================================
    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.isBlank() || codigo.length() < CODIGO_MINIMO || codigo.length() > CODIGO_MAXIMO) {
            throw new IllegalArgumentException("O código deve ter entre " + CODIGO_MINIMO + " e " + CODIGO_MAXIMO + " caracteres.");
        }
        validarRegex(codigo, CODIGO_VALIDO, "Código do cupom inválido. Use apenas letras maiúsculas e números.");
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank() || descricao.length() < DESCRICAO_MINIMO || descricao.length() > DESCRICAO_MAXIMO) {
            throw new IllegalArgumentException("A descrição deve ter entre " + DESCRICAO_MINIMO + " e " + DESCRICAO_MAXIMO + " caracteres.");
        }
        validarRegex(descricao, DESCRICAO_VALIDA, "O formato da descrição informado é inválido.");
    }

    private void validarValorMinimoPedido(BigDecimal valorMinimoPedido) {
        if (valorMinimoPedido == null) {
            throw new IllegalArgumentException("O valor mínimo do pedido não pode ser nulo.");
        }
        if (valorMinimoPedido.compareTo(VALORPEDIDO_MINIMO) < 0) {
            throw new IllegalArgumentException("O valor mínimo do pedido não pode ser negativo.");
        }
        if (valorMinimoPedido.compareTo(VALORPEDIDO_MAXIMO) > 0) {
            throw new IllegalArgumentException("O valor do pagamento excede o limite máximo permitido.");
        }
    }

    public void validarLimiteUso(int limiteUso) {
        if (limiteUso <= 0) {
            throw new IllegalArgumentException("O limite de uso deve ser maior que zero.");
        }
    }

    private void validarPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas do período não podem ser nulas.");
        }
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("A data inicial não pode ser maior que a data final.");
        }
    }

    private void validarQuantidadeUtilizada(int quantidadeUtilizada, int limiteUso) {
        if (quantidadeUtilizada < 0) {
            throw new IllegalArgumentException("Quantidade utilizada inválida.");
        }
        if (quantidadeUtilizada > limiteUso) {
            throw new IllegalArgumentException("Quantidade utilizada maior que o limite permitido.");
        }
    }

    private void validarTipoDesconto(TipoDesconto tipoDesconto) {
        if (tipoDesconto == null) {
            throw new IllegalArgumentException("O tipo de desconto não pode ser nulo.");
        }
    }

    private void validarPercentualDesconto(BigDecimal valorDesconto) {
        if (valorDesconto.compareTo(PERCENTUAL_MINIMO) < 0 || valorDesconto.compareTo(PERCENTUAL_MAXIMO) > 0) {
            throw new IllegalArgumentException("O percentual de desconto deve estar entre " + PERCENTUAL_MINIMO + "% e " + PERCENTUAL_MAXIMO + "%.");
        }
    }

    private void validarValorFixoDesconto(BigDecimal valorDesconto) {
        if (valorDesconto.compareTo(BigDecimal.ZERO) <= 0 || valorDesconto.compareTo(DESCONTO_FIXO_MAXIMO) > 0) {
            throw new IllegalArgumentException("O valor do desconto fixo deve ser maior que zero e menor que o limite máximo permitido.");
        }
    }

    private void validarDescontoEstrategia(TipoDesconto tipo, BigDecimal valor, BigDecimal minimoPedido) {
        validarTipoDesconto(tipo);
        if (valor == null) {
            throw new IllegalArgumentException("O valor do desconto não pode ser nulo.");
        }
        
        if (tipo == TipoDesconto.PERCENTUAL) {
            validarPercentualDesconto(valor);
        } else if (tipo == TipoDesconto.VALOR_FIXO) {
            validarValorFixoDesconto(valor);
            if (valor.compareTo(minimoPedido) > 0) {
                throw new IllegalArgumentException("O valor do desconto fixo não pode ser maior que o valor mínimo do pedido.");
            }
        }
    }

    // ==========================================
    //       MÉTODOS DE ALTERAÇÃO DE DADOS
    // ==========================================
    public void alterarCodigo(String novoCodigo) {
        Objects.requireNonNull(novoCodigo, "O novo código não pode ser nulo.");
        String codigoTratado = normalizarTexto(novoCodigo).toUpperCase();
        validarCodigo(codigoTratado);
        this.codigo = codigoTratado;
        atualizarDataAtualizacao();
    }

    public void alterarDescricao(String novaDescricao) {
        Objects.requireNonNull(novaDescricao, "A nova descrição não pode ser nula.");
        String descricaoTratada = normalizarTexto(novaDescricao);
        validarDescricao(descricaoTratada);
        this.descricao = descricaoTratada;
        atualizarDataAtualizacao();
    }

    public void alterarTipoDesconto(TipoDesconto novoTipo) {
        validarDescontoEstrategia(novoTipo, this.valorDesconto, this.valorMinimoPedido);
        this.tipoDesconto = novoTipo;
        atualizarDataAtualizacao();
    }

    public void alterarValorDesconto(BigDecimal novoValor) {
        validarDescontoEstrategia(this.tipoDesconto, novoValor, this.valorMinimoPedido);
        this.valorDesconto = novoValor.setScale(2, ARREDONDAMENTO);
        atualizarDataAtualizacao();
    }

    public void alterarValorMinimoPedido(BigDecimal novoValorMinimo) {
        validarValorMinimoPedido(novoValorMinimo);
        if (this.tipoDesconto == TipoDesconto.VALOR_FIXO && this.valorDesconto.compareTo(novoValorMinimo) > 0) {
            throw new IllegalArgumentException("O valor mínimo do pedido não pode ser menor que o desconto fixo atual.");
        }
        this.valorMinimoPedido = novoValorMinimo.setScale(2, ARREDONDAMENTO);
        atualizarDataAtualizacao();
    }

    public void alterarLimiteUso(int novoLimite) {
        validarLimiteUso(novoLimite);
        if (novoLimite < this.quantidadeUtilizada) {
            throw new IllegalArgumentException("O novo limite de uso não pode ser menor que a quantidade já utilizada.");
        }
        this.limiteUso = novoLimite;
        atualizarDataAtualizacao();
    }

    public void alterarPeriodo(LocalDateTime novaDataInicio, LocalDateTime novaDataFim) {
        validarPeriodo(novaDataInicio, novaDataFim);
        this.dataInicio = novaDataInicio;
        this.dataFim = novaDataFim;
        atualizarDataAtualizacao();
    }

    public void alterarDataInicio(LocalDateTime novaDataInicio) {
        validarPeriodo(novaDataInicio, this.dataFim);
        this.dataInicio = novaDataInicio;
        atualizarDataAtualizacao();
    }

    public void alterarDataFim(LocalDateTime novaDataFim) {
        validarPeriodo(this.dataInicio, novaDataFim);
        this.dataFim = novaDataFim;
        atualizarDataAtualizacao();
    }

    // ==========================================
    //       MÉTODOS DE CONTROLE DE UTILIZAÇÃO
    // ==========================================
    public void registrarUtilizacao() {
        if (!podeSerUtilizado()) {
            throw new IllegalStateException("Cupom indisponível para uso.");
        }
        this.quantidadeUtilizada++;
        atualizarDataAtualizacao();
    }

    public void cancelarUtilizacao() {
        if (this.quantidadeUtilizada <= 0) {
            throw new IllegalStateException("Não há utilizações registradas para cancelar.");
        }
        this.quantidadeUtilizada--;
        atualizarDataAtualizacao();
    }

    public void zerarUtilizacoes() {
        this.quantidadeUtilizada = 0;
        atualizarDataAtualizacao();
    }

    // ==========================================
    //            MÉTODOS DE STATUS
    // ==========================================
    public void ativar() {
        if (!this.ativo) {
            this.ativo = true;
            atualizarDataAtualizacao();
        }
    }

    public void desativar() {
        if (this.ativo) {
            this.ativo = false;
            atualizarDataAtualizacao();
        }
    }

    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(this.dataFim);
    }

    public boolean atingiuLimiteUso() {
        return this.quantidadeUtilizada >= this.limiteUso;
    }

    public boolean estaVigente() {
        LocalDateTime agora = LocalDateTime.now();
        return !agora.isBefore(this.dataInicio) && !agora.isAfter(this.dataFim);
    }

    public boolean estaAtivo() {
        return this.ativo;
    }

    public boolean podeSerUtilizado() {
        return estaAtivo() && estaVigente() && !atingiuLimiteUso();
    }

    // ==========================================
    //            MÉTODOS DE CÁLCULO
    // ==========================================
    public BigDecimal calcularDesconto(BigDecimal valorPedido) {
        if (valorPedido == null) {
            throw new IllegalArgumentException("O valor do pedido não pode ser nulo.");
        }
        
        // Aplica o fluxo de validação da regra de negócio
        if (!podeSerUtilizado() || valorPedido.compareTo(this.valorMinimoPedido) < 0) {
            return BigDecimal.ZERO.setScale(2, ARREDONDAMENTO);
        }

        if (this.tipoDesconto == TipoDesconto.PERCENTUAL) {
            // Cálculo: (valorPedido * valorDesconto) / 100
            BigDecimal descontoCalculado = valorPedido.multiply(this.valorDesconto)
                    .divide(new BigDecimal("100.00"), ARREDONDAMENTO);
            
            // Regra de segurança extra: o desconto percentual não pode passar do valor total do pedido
            if (descontoCalculado.compareTo(valorPedido) > 0) {
                return valorPedido.setScale(2, ARREDONDAMENTO);
            }
            return descontoCalculado.setScale(2, ARREDONDAMENTO);
            
        } else if (this.tipoDesconto == TipoDesconto.VALOR_FIXO) {
            // Se o desconto fixo for maior que o pedido por alguma anomalia, zera o valor do pedido
            if (this.valorDesconto.compareTo(valorPedido) > 0) {
                return valorPedido.setScale(2, ARREDONDAMENTO);
            }
            return this.valorDesconto.setScale(2, ARREDONDAMENTO);
        }

        return BigDecimal.ZERO.setScale(2, ARREDONDAMENTO);
    }

    public BigDecimal aplicarDesconto(BigDecimal valorPedido) {
        if (valorPedido == null) {
            throw new IllegalArgumentException("O valor do pedido não pode ser nulo.");
        }
        BigDecimal desconto = calcularDesconto(valorPedido);
        BigDecimal valorFinal = valorPedido.subtract(desconto);
        
        if (valorFinal.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, ARREDONDAMENTO);
        }
        return valorFinal.setScale(2, ARREDONDAMENTO);
    }

    // ==========================================
    //       MÉTODOS AUXILIARES PRIVADOS
    // ==========================================
    private void validarRegex(String texto, Pattern pattern, String mensagemErro) {
        Objects.requireNonNull(texto, "Texto não pode ser nulo.");
        Objects.requireNonNull(pattern, "Pattern não pode ser nulo.");

        if (!pattern.matcher(texto).matches()) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    private String normalizarTexto(String texto) {
        Objects.requireNonNull(texto, "Texto não pode ser nulo.");
        return texto.replaceAll("\\s+", " ").trim();
    }

    private void atualizarDataAtualizacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // ==========================================
    //                 GETTERS
    // ==========================================
    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public TipoDesconto getTipoDesconto() { return tipoDesconto; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public BigDecimal getValorMinimoPedido() { return valorMinimoPedido; }
    public int getLimiteUso() { return limiteUso; }
    public int getQuantidadeUtilizada() { return quantidadeUtilizada; }
    public boolean isAtivo() { return ativo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }

    // ==========================================
    //             OBJECT METHODS
    // ==========================================
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cupom other)) return false;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Cupom{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", ativo=" + ativo +
                ", quantidadeUtilizada=" + quantidadeUtilizada +
                ", limiteUso=" + limiteUso +
                '}';
    }
}






