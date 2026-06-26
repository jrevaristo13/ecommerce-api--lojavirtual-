package br.com.ecommerce.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class Endereco {

    // ==========================================
    // BLOCO DE CONSTANTES DE VALIDAÇÃO
    // ==========================================
    private static final Pattern CEP_VALIDO = Pattern.compile("^\\d{5}-\\d{3}$|^\\d{8}$");
    private static final Pattern ESTADO_VALIDO = Pattern.compile("^[A-Z]{2}$"); 

    // ==========================================
    // BLOCO DE ESTADO (IMUTÁVEL)
    // ==========================================
    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cidade;
    private final String estado;
    private final String cep;

    // ==========================================
    // CONSTRUTOR UNIFICADO (VALIDAÇÃO E SANITIZAÇÃO)
    // ==========================================
    public Endereco(String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) {
        // 1. Fail-Fast contra nulos (Campos obrigatórios)
        Objects.requireNonNull(logradouro, "Logradouro não pode ser nulo.");
        Objects.requireNonNull(numero, "Número não pode ser nulo.");
        Objects.requireNonNull(bairro, "Bairro não pode ser nulo.");
        Objects.requireNonNull(cidade, "Cidade não pode ser nulo.");
        Objects.requireNonNull(estado, "Estado não pode ser nulo.");
        Objects.requireNonNull(cep, "CEP não pode ser nulo.");

        // 2. Sanitização (Tratamento dos dados brutos)
        this.logradouro = logradouro.trim();
        this.numero = numero.trim();
        this.complemento = complemento != null ? complemento.trim() : "";
        this.bairro = bairro.trim();
        this.cidade = cidade.trim();
        this.estado = estado.trim().toUpperCase();
        // Remove espaços extras que possam vir no CEP
        this.cep = cep.trim(); 

        // 3. Validações de Regra de Negócio (Garante consistência pós-sanitização)
        validarEndereco();
    }

    // ==========================================
    // VALIDAÇÕES PRIVADAS
    // ==========================================
    private void validarEndereco() {
        if (this.logradouro.isEmpty()) {
            throw new IllegalArgumentException("O logradouro não pode estar em branco.");
        }
        if (this.numero.isEmpty()) {
            throw new IllegalArgumentException("O número não pode estar em branco.");
        }
        if (this.bairro.isEmpty()) {
            throw new IllegalArgumentException("O bairro não pode estar em branco.");
        }
        if (this.cidade.isEmpty()) {
            throw new IllegalArgumentException("A cidade não pode estar em branco.");
        }
        if (!ESTADO_VALIDO.matcher(this.estado).matches()) {
            throw new IllegalArgumentException("O estado deve ser a sigla com duas letras maiúsculas (ex: SP, RJ).");
        }
        if (!CEP_VALIDO.matcher(this.cep).matches()) {
            throw new IllegalArgumentException("O formato do CEP é inválido. Use 00000-000 ou apenas números.");
        }
    }

    // ==========================================
    // GETTERS (APENAS LEITURA)
    // ==========================================
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCep() { return cep; }

    // ==========================================
    // CONTRATOS DO OBJETO (VALUE OBJECT EQUALITY)
    // ==========================================
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Endereco other)) return false; 
        
        return Objects.equals(this.logradouro, other.logradouro) &&
               Objects.equals(this.numero, other.numero) &&
               Objects.equals(this.complemento, other.complemento) &&
               Objects.equals(this.bairro, other.bairro) &&
               Objects.equals(this.cidade, other.cidade) &&
               Objects.equals(this.estado, other.estado) &&
               Objects.equals(this.cep, other.cep);
    }

  @Override
public int hashCode() {
    return Objects.hash(logradouro, numero, complemento, bairro, cidade, estado, cep);
}

    @Override
    public String toString() {
        return logradouro + ", Nº " + numero + (complemento.isEmpty() ? "" : " (" + complemento + ")") + 
               " - " + bairro + ", " + cidade + "/" + estado + " - CEP: " + cep;
    }
}
