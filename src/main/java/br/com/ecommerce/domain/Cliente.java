package br.com.ecommerce.domain;

import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

public class Cliente {

    // ==========================================
    // BLOCO DE CONSTANTES DE REGRAS
    // ==========================================
    private static final int NOME_MINIMO = 3;
    private static final int NOME_MAXIMO = 150;
    private static final int EMAIL_MINIMO = 5;
    private static final int EMAIL_MAXIMO = 100;

    private static final Pattern NOME_VALIDO = Pattern.compile("^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$");
    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern CPF_LIMPO = Pattern.compile("^\\d{11}$");
    private static final Pattern CPF_FORMATADO = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");

    // ==========================================
    // BLOCO DE IDENTIDADE E ESTADO
    // ==========================================
    private final UUID id;
    private String nome;
    private String email;
    private final String documento; // Tornado final pois CPF é uma identidade imutável do cliente
    private boolean ativo;
    private final LocalDateTime dataCadastro; 
    private LocalDateTime ultimoAcesso;
    private final List<Endereco> enderecos = new ArrayList<>();

    // ==========================================
    // CONSTRUTORES (Garante Estado Sempre Válido)
    // ==========================================

    // JPA/Hibernate Constructor (Necessário se for usar ORM futuramente)
    protected Cliente() {
        this.id = null;
        this.documento = null;
        this.dataCadastro = null;
    }

    // Bloco de Criação (Novo Cliente)
    public Cliente(String nome, String email, String documento) {
        this(UUID.randomUUID(), nome, email, documento, true, new ArrayList<>());
    }
       
    // Bloco de Reconstituição (Carregar do Banco de Dados)
    public Cliente(UUID id, String nome, String email, String documento, boolean ativo, List<Endereco> enderecos) {
        
        if (id == null) {
            throw new IllegalArgumentException("O ID não pode ser nulo.");
        }
        if (enderecos == null) {
            throw new IllegalArgumentException("A lista de endereços não pode ser nula.");
        }

        // Sanitização e Validação das Strings
        String nomeSanitizado = sanitizarCampos(nome);
        validarTamanhoNome(nomeSanitizado);
        validarRegex(nomeSanitizado, NOME_VALIDO, "O formato do nome informado é inválido.");

        String emailSanitizado = sanitizarCampos(email);
        validarTamanhoEmail(emailSanitizado);
        validarRegex(emailSanitizado, EMAIL_VALIDO, "O formato do e-mail informado é inválido.");

        validarCpf(documento);

        // Atribuição Segura
        this.id = id;
        this.nome = nomeSanitizado;
        this.email = emailSanitizado;
        this.documento = limparMascaraDocumento(documento); // Salva apenas os números (padrão de banco)
        this.ativo = ativo;
        this.dataCadastro = LocalDateTime.now(); 
        this.ultimoAcesso = LocalDateTime.now();
        this.enderecos.addAll(enderecos);
    }

    // ==========================================
    // BLOCO DE CONSULTA DE ESTADO (Getters)
    // ==========================================
    
    public UUID getId() { 
        return id; 
    }
    
    public String getNome() { 
        return nome; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public String getDocumento() { 
        return documento; 
    }
    
    public boolean isAtivo() { 
        return ativo; 
    }
    
    public LocalDateTime getDataCadastro() { 
        return dataCadastro; 
    }
    
    public LocalDateTime getUltimoAcesso() { 
        return ultimoAcesso; 
    }
    
    // Protege a lista interna contra modificações externas diretas
    public List<Endereco> getEnderecos() { 
        return Collections.unmodifiableList(enderecos); 
    }

    // ==========================================
    // COMPORTAMENTOS SEMÂNTICOS (Regras de Negócio)
    // ==========================================
    
    public void alterarNome(String novoNome) {
        validarEstadoAtivo();
        String nomeSanitizado = sanitizarCampos(novoNome);

        validarTamanhoNome(nomeSanitizado);
        validarRegex(nomeSanitizado, NOME_VALIDO, "O formato do nome informado é inválido.");
        
        this.nome = nomeSanitizado;
    }

    public void alterarEmail(String novoEmail) {
        validarEstadoAtivo();
        String emailSanitizado = sanitizarCampos(novoEmail);
        
        validarTamanhoEmail(emailSanitizado);
        validarRegex(emailSanitizado, EMAIL_VALIDO, "O formato do e-mail informado é inválido.");
        
        this.email = emailSanitizado;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }

    public void atualizarUltimoAcesso() {
        validarEstadoAtivo();
        this.ultimoAcesso = LocalDateTime.now();
    }

    // ==========================================
    // GERENCIAMENTO DE ENDEREÇOS (Encapsulado)
    // ==========================================

    public void adicionarEndereco(Endereco endereco) {
        validarEstadoAtivo();
        if (endereco == null) {
            throw new IllegalArgumentException("Não é possível adicionar um endereço nulo.");
        }
        if (this.enderecos.contains(endereco)) {
            throw new IllegalArgumentException("Este endereço já está cadastrado para este cliente.");
        }
        this.enderecos.add(endereco);
    }

    public void removerEndereco(Endereco endereco) {
        validarEstadoAtivo();
        if (endereco == null) {
            throw new IllegalArgumentException("Não é possível remover um endereço nulo.");
        }
        if (!this.enderecos.contains(endereco)) {
            throw new IllegalArgumentException("O endereço informado não pertence a este cliente.");
        }
        this.enderecos.remove(endereco);
    }

    // ==========================================
    // SANITIZAÇÃO E VALIDAÇÕES PRIVADAS
    // ==========================================

    private String sanitizarCampos(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("O campo não pode ser nulo.");
        }
        return texto.trim()
                    .replaceAll("<[^>]*>", "") // Proteção básica contra XSS
                    .replaceAll("['\"`;\\-]", ""); // Auxilia contra SQL Injection estrutural
    }

    private String limparMascaraDocumento(String documento) {
        if (documento == null) {
            return "";
        }
        return documento.replaceAll("[^\\d]", "");
    }

    private void validarCpf(String documento) { 
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("O documento não pode ser nulo ou vazio.");
        }

        boolean formatoValido = CPF_LIMPO.matcher(documento).matches() || CPF_FORMATADO.matcher(documento).matches();
        if (!formatoValido) {
            throw new IllegalArgumentException("O formato do CPF é inválido. Use apenas números ou o padrão 000.000.000-00.");
        }

        String cpfApenasNumeros = limparMascaraDocumento(documento);

        if (!isCpfMatematicamenteValido(cpfApenasNumeros)) {
            throw new IllegalArgumentException("O número de CPF fornecido é inválido.");
        }
    }

    private boolean isCpfMatematicamenteValido(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Primeiro dígito verificador
            int soma = 0, peso = 10;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }
            int resto = (soma * 10) % 11;
            int dv1 = (resto == 10) ? 0 : resto;

            // Segundo dígito verificador
            soma = 0; peso = 11;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }
            resto = (soma * 10) % 11;
            int dv2 = (resto == 10) ? 0 : resto;

            int d1Enviado = Character.getNumericValue(cpf.charAt(9));
            int d2Enviado = Character.getNumericValue(cpf.charAt(10));

            return (dv1 == d1Enviado && dv2 == d2Enviado);
        } catch (Exception e) {
            return false;
        }
    }

    private void validarTamanhoNome(String nome) {
        int tamanho = nome.length();
        if (tamanho < NOME_MINIMO || tamanho > NOME_MAXIMO) {
            throw new IllegalArgumentException("O nome deve ter entre " + NOME_MINIMO + " e " + NOME_MAXIMO + " caracteres.");
        }
    }

    private void validarTamanhoEmail(String email) {
        int tamanho = email.length();
        if (tamanho < EMAIL_MINIMO || tamanho > EMAIL_MAXIMO) {
            throw new IllegalArgumentException("O e-mail deve ter entre " + EMAIL_MINIMO + " e " + EMAIL_MAXIMO + " caracteres.");
        }
    }

    private void validarEstadoAtivo() {
        if (!this.ativo) {
            throw new IllegalStateException("Operação negada: Não é possível modificar dados de um cliente inativo.");
        }
    }

    private void validarRegex(String texto, Pattern pattern, String mensagemErro) {
        if (texto == null || !pattern.matcher(texto).matches()) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    // ==========================================
    // MÉTODOS ESTRUTURAIS (Identity Base)
    // ==========================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id) || Objects.equals(documento, cliente.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, documento);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", documento='" + documento + '\'' +
                ", ativo=" + ativo +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}


