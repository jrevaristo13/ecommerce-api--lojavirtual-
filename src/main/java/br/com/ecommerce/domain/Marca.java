package br.com.ecommerce.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Marca {
    
    // ==========================================
    // BLOCO DE CONSTANTES DE REGRAS
    // ==========================================
    private static final int NOME_MINIMO = 3;
    private static final int NOME_MAXIMO = 150;
    private static final int DESCRICAO_MINIMO = 3;
    private static final int DESCRICAO_MAXIMO = 500; 
    private static final int TELEFONE_MINIMO = 10;
    private static final int TELEFONE_MAXIMO = 11;
  
    private static final Pattern NOME_VALIDO = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ\\s'-]{3,150}$");
    private static final Pattern DESCRICAO_VALIDO = Pattern.compile("^[\\p{L}0-9\\s.,()'-]{3,500}$");
    private static final Pattern EMAIL_VALIDO = Pattern.compile(
        "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );
    private static final Pattern REGEX_TELEFONE = Pattern.compile("^(?:55)?([1-9]{2})([9]?\\d{8})$");
    private static final Pattern REPETIDOS = Pattern.compile("^(\\d)\\1+$");

    // ==========================================
    //       BLOCO DE IDENTIDADE E ESTADO
    // ==========================================
    private final UUID id;
    private String nome;
    private String descricao;
    private String siteOficial;
    private String emailContato;
    private String telephoneContato; 
    private String logoUrl;
    private boolean ativa; 
    private final LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    // ==========================================
    // CONSTRUTOR DE CRIAÇÃO (Novas Marcas)
    // ==========================================
    public Marca(String nome, String descricao, String siteOficial, String emailContato, String telefoneContato,
                 String logoUrl, boolean ativa) {

        Objects.requireNonNull(nome, "O nome de cadastro não pode ser nulo.");
        Objects.requireNonNull(descricao, "A descrição da marca não pode ser nula.");
        Objects.requireNonNull(siteOficial, "O site oficial da marca não pode ser nulo.");
        Objects.requireNonNull(emailContato, "O e-mail da marca não pode ser nulo.");
        Objects.requireNonNull(telefoneContato, "O telefone da marca não pode ser nulo.");
        Objects.requireNonNull(logoUrl, "A URL do logo não pode ser nula.");

        this.id = UUID.randomUUID();
        
        String nomeTratado = normalizarTexto(nome);
        String descricaoTratada = normalizarTexto(descricao);
        String siteTratado = siteOficial.trim();
        String emailTratado = emailContato.trim().toLowerCase();
        String telefoneTratado = telefoneContato.replaceAll("\\D", "");
        String logoTratada = logoUrl.trim();

        validarNome(nomeTratado);
        validarDescricao(descricaoTratada);
        validarSite(siteTratado);
        validarEmailContato(emailTratado);
        validarTelefoneContato(telefoneTratado);
        validarLogoUrl(logoTratada);

        this.nome = nomeTratado;
        this.descricao = descricaoTratada;
        this.siteOficial = siteTratado;
        this.emailContato = emailTratado;
        this.telephoneContato = telefoneTratado;
        this.logoUrl = logoTratada;
        this.ativa = ativa;
        
        this.dataCadastro = LocalDateTime.now();
        this.dataAtualizacao = this.dataCadastro; 
    }

    // ==========================================
    // CONSTRUTOR DE RECONSTITUIÇÃO (Banco de Dados)
    // ==========================================
    public Marca(UUID id, String nome, String descricao, String siteOficial, String emailContato, 
                 String telephoneContato, String logoUrl, boolean ativa, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {
        this.id = Objects.requireNonNull(id, "O ID da marca não pode ser nulo.");
        this.nome = nome;
        this.descricao = descricao;
        this.siteOficial = siteOficial;
        this.emailContato = emailContato;
        this.telephoneContato = telephoneContato;
        this.logoUrl = logoUrl;
        this.ativa = ativa;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
    }
 
    // ==========================================
    // MÉTODOS DE NEGÓCIO (STATUS)
    // ==========================================
    public void activar() { getAtivar(); }
    
    public void getAtivar() {
        if (!this.ativa) {
            this.ativa = true;
            atualizarDataAtualizacao();
        }
    }

    public void desativar() {
        if (this.ativa) {
            this.ativa = false;
            atualizarDataAtualizacao();
        }
    }

    // ==========================================
    // MÉTODOS DE ALTERAÇÃO DE ESTADO (DDD)
    // ==========================================
    public void alterarNome(String novoNome) {
        Objects.requireNonNull(novoNome, "O novo nome não pode ser nulo.");
        String tratado = normalizarTexto(novoNome);
        validarNome(tratado);
        this.nome = tratado;
        atualizarDataAtualizacao();
    }

    public void alterarDescricao(String novaDescricao) {
        Objects.requireNonNull(novaDescricao, "A nova descrição não pode ser nula.");
        String tratada = normalizarTexto(novaDescricao);
        validarDescricao(tratada);
        this.descricao = tratada;
        atualizarDataAtualizacao();
    }

    public void alterarSite(String novoSite) {
        Objects.requireNonNull(novoSite, "O novo site não pode ser nulo.");
        String tratado = novoSite.trim();
        validarSite(tratado);
        this.siteOficial = tratado;
        atualizarDataAtualizacao();
    }

    public void alterarEmail(String novoEmail) {
        Objects.requireNonNull(novoEmail, "O novo e-mail não pode ser nulo.");
        String tratado = novoEmail.trim().toLowerCase();
        validarEmailContato(tratado);
        this.emailContato = tratado;
        atualizarDataAtualizacao();
    }

    public void alterarTelefone(String novoTelefone) {
        Objects.requireNonNull(novoTelefone, "O novo telefone não pode ser nulo.");
        String tratado = novoTelefone.replaceAll("\\D", "");
        validarTelefoneContato(tratado);
        this.telephoneContato = tratado;
        atualizarDataAtualizacao();
    }

    public void alterarLogo(String novaLogoUrl) {
        Objects.requireNonNull(novaLogoUrl, "A nova URL do logo não pode ser nula.");
        String tratada = novaLogoUrl.trim();
        validarLogoUrl(tratada);
        this.logoUrl = tratada;
        atualizarDataAtualizacao();
    }

    // ==========================================
    // VALIDAÇÕES PRIVADAS (GATES)
    // ==========================================
    private void validarNome(String nome) {
        if (nome == null || nome.isBlank() || nome.length() < NOME_MINIMO || nome.length() > NOME_MAXIMO) {
            throw new IllegalArgumentException("O nome deve ter entre " + NOME_MINIMO + " e " + NOME_MAXIMO + " caracteres.");
        }
        validarRegex(nome, NOME_VALIDO, "O formato do nome informado é inválido.");
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank() || descricao.length() < DESCRICAO_MINIMO || descricao.length() > DESCRICAO_MAXIMO) {
            throw new IllegalArgumentException("A descrição deve ter entre " + DESCRICAO_MINIMO + " e " + DESCRICAO_MAXIMO + " caracteres.");
        }
        validarRegex(descricao, DESCRICAO_VALIDO, "O formato da descrição informado é inválido.");
    }

    private void validarSite(String site) {
        if (site == null || site.isBlank()) {
            throw new IllegalArgumentException("Site oficial é obrigatório.");
        }
        if (!site.startsWith("http://") && !site.startsWith("https://")) {
            throw new IllegalArgumentException("URL do site inválida. Deve iniciar com http:// ou https://");
        }
    }

    private void validarEmailContato(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        validarRegex(email, EMAIL_VALIDO, "O formato do e-mail informado é inválido.");
    }

    private void validarTelefoneContato(String telephone) {
        if (telephone == null || telephone.isBlank() || telephone.length() < TELEFONE_MINIMO || telephone.length() > TELEFONE_MAXIMO) {
            throw new IllegalArgumentException("O telefone deve ter entre " + TELEFONE_MINIMO + " e " + TELEFONE_MAXIMO + " dígitos.");
        }
        validarRegex(telephone, REGEX_TELEFONE, "Telefone inválido. Insira um formato DDD + Número válido.");
        if (REPETIDOS.matcher(telephone).matches()) {
            throw new IllegalArgumentException("Número de telefone inválido (sequência repetida).");
        }
    }

    private void validarLogoUrl(String logoUrl) {
        if (logoUrl == null || logoUrl.isBlank()) {
            throw new IllegalArgumentException("A URL do logo é obrigatória.");
        }
        if (!logoUrl.startsWith("http://") && !logoUrl.startsWith("https://")) {
            throw new IllegalArgumentException("URL do logo inválida. Deve iniciar com http:// ou https://");
        }
    }

    private void validarRegex(String texto, Pattern pattern, String mensagemErro) {
        if (!pattern.matcher(texto).matches()) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    private String normalizarTexto(String texto) {
        return texto.replaceAll("\\s+", " ").trim();
    }

    private void atualizarDataAtualizacao() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // ==========================================
    //             OBJECT METHODS
    // ==========================================
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Marca other = (Marca) obj;
        if (this.id == null || other.id == null) return false;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Marca{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", ativa=" + ativa +
                '}';
    }

    // ==========================================
    // GETTERS
    // ==========================================
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getSiteOficial() { return siteOficial; }
    public String getEmailContato() { return emailContato; }
    public String getTelefoneContato() { return telephoneContato; }
    public String getLogoUrl() { return logoUrl; }
    public boolean isAtiva() { return ativa; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
}