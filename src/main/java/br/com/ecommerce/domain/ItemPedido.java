package br.com.ecommerce.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class ItemPedido {

    // ==========================================
    //       CONSTANTES DE REGRA DE NEGÓCIO
    // ==========================================
    private static final int NOMEPRODUTO_MINIMO = 3;
    private static final int NOMEPRODUTO_MAXIMO = 50;
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;
    private static final BigDecimal PRECOUNITARIO_MINIMO = BigDecimal.ZERO; 
    private static final BigDecimal PRECOUNITARIO_MAXIMO = new BigDecimal("1000000.00");

    // ==========================================
    //        IDENTIDADE E ESTADO
    // ==========================================
    private final UUID id;
    private final UUID produtoId;
    private String nomeProduto;
    private BigDecimal precoUnitario;
    private int quantidade;
    private BigDecimal subtotal;

    // ========================================== 
    //       CONSTRUTOR PRINCIPAL (CRIAÇÃO)
    // ==========================================
    public ItemPedido(UUID produtoId, String nomeProduto, BigDecimal precoUnitario, int quantidade) {
        
        Objects.requireNonNull(produtoId, "ProdutoId não pode ser nulo");
        
        this.id = UUID.randomUUID();
        this.produtoId = produtoId;
        
        // Utiliza os próprios comportamentos para garantir a aplicação das regras e sanitização
        alterarNomeProduto(nomeProduto);
        alterarPreco(precoUnitario);
        
        // Valida e define a quantidade inicial
        validarQuantidade(quantidade);
        this.quantidade = quantidade;
        
        // Calcula o subtotal inicial de forma automática e segura
        recalcularSubtotal();
    }

    // ==========================================
    //    CONSTRUTOR DE RECONSTITUIÇÃO 
    // ==========================================
    public ItemPedido(UUID id, UUID produtoId, String nomeProduto, BigDecimal precoUnitario, int quantidade, BigDecimal subtotal) {
        
        
        this.id = Objects.requireNonNull(id, "Id não pode ser nulo");
        this.produtoId = Objects.requireNonNull(produtoId, "ProdutoId não pode ser nulo");
        
        alterarNomeProduto(nomeProduto);
        alterarPreco(precoUnitario);
        
        validarQuantidade(quantidade);
        this.quantidade = quantidade;
        
        // Na reconstituição, apenas valida se o preço do subtotal que veio do banco é válido
        validarPreco(subtotal);
        this.subtotal = subtotal.setScale(2, ARREDONDAMENTO);
    }

    // ==========================================
    //             COMPORTAMENTOS
    // ==========================================
    
    public void alterarNomeProduto(String novoNomeProduto) {
        String nomeProdutoTratado = normalizarTexto(novoNomeProduto);
        validarNome(nomeProdutoTratado);
        this.nomeProduto = nomeProdutoTratado;
    }

    public void alterarPreco(BigDecimal novoPrecoUnitario) {
        validarPreco(novoPrecoUnitario);
        
        // Teto de preço (Regra de negócio que estava na sua validação)
        if (novoPrecoUnitario.compareTo(PRECOUNITARIO_MAXIMO) > 0) {
            novoPrecoUnitario = PRECOUNITARIO_MAXIMO; 
        }
        
        this.precoUnitario = novoPrecoUnitario.setScale(2, ARREDONDAMENTO);
        recalcularSubtotal(); // Se mudar o preço, o subtotal muda!
    }

    public void adicionarQuantidade(int novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a ser adicionada deve ser maior que zero.");
        }
        
        this.quantidade += novaQuantidade;
        recalcularSubtotal(); // Se mudar a quantidade, o subtotal muda!
    }

    // Centraliza a regra matemática do subtotal para evitar falhas humanas
    private void recalcularSubtotal() {
        this.subtotal = this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade)).setScale(2, ARREDONDAMENTO);
    }

    // ==========================================
    //         SANITIZAÇÃO / FUNÇÕES
    // ==========================================
    private String normalizarTexto(String texto) {
        Objects.requireNonNull(texto, "Texto não pode ser nulo.");
        return texto.replaceAll("\\s+", " ").trim();
    }

    private void validarRegex(String texto, Pattern pattern, String mensagemErro) {
        if (!pattern.matcher(texto).matches()) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    // ==========================================
    //             VALIDAÇÕES PRIVADAS
    // ==========================================
    private void validarNome(String nomeProduto) {
        if (nomeProduto == null || nomeProduto.isBlank()) {
            throw new IllegalArgumentException("O nome do produto não pode ser nulo nem vazio.");
        }

        if (nomeProduto.length() < NOMEPRODUTO_MINIMO || nomeProduto.length() > NOMEPRODUTO_MAXIMO) {
            throw new IllegalArgumentException(
                    "O nome deve ter entre " + NOMEPRODUTO_MINIMO + " e " + NOMEPRODUTO_MAXIMO + " caracteres."
            );
        }
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
    }

    private void validarPreco(BigDecimal preco) {
        if (preco == null) {
            throw new IllegalArgumentException("O valor/preço não pode ser nulo.");
        }

        if (preco.compareTo(PRECOUNITARIO_MINIMO) <= 0) {
            throw new IllegalArgumentException("O valor/preço deve ser maior que zero.");
        }
    }

    // ==========================================
    //             OBJECT METHODS
    // ==========================================

    @Override
    public boolean equals(Object obj) {
        // 1. Verificação de referência em memória
        if (this == obj) return true;
        
        // 2. Verificação de nulidade e igualdade de classe (evita problemas com proxies do Hibernate)
        if (obj == null || getClass() != obj.getClass()) return false;
        
        // 3. Cast seguro
        ItemPedido other = (ItemPedido) obj;
        
        // 4. Se os IDs principais forem nulos, não podemos afirmar que são iguais
        if (this.id == null || other.id == null || this.produtoId == null || other.produtoId == null) {
            return false;
        }
        
        // 5. Consideramos o mesmo item se o ID da instância E o ID do produto forem iguais
        return Objects.equals(this.id, other.id) && 
               Objects.equals(this.produtoId, other.produtoId);
    }

    @Override
    public int hashCode() {
        // Alinhado estritamente com as variáveis do método equals
        return Objects.hash(this.id, this.produtoId);
    }

    @Override
    public String toString() {
        return "ItemPedido{" +
                "id=" + id +
                ", produtoId=" + produtoId +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", precoUnitario=" + precoUnitario +
                ", quantidade=" + quantidade +
                ", subtotal=" + subtotal +
                '}';
    }

    // ==========================================
    //                 GETTERS
    // ==========================================
    public UUID getId() { return id; }
    public UUID getProdutoId() { return produtoId; }
    public String getNomeProduto() { return nomeProduto; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getSubtotal() { return subtotal; }
}