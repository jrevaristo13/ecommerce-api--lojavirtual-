package br.com.ecommerce.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Categoria {

    // ==========================================
    // CONSTANTES DE REGRA DE NEGÓCIO
    // ==========================================
    private static final int NOME_MINIMO = 3;
    private static final int NOME_MAXIMO = 50;

    private static final int DESCRICAO_MINIMA = 10;
    private static final int DESCRICAO_MAXIMA = 4000;

    private static final Pattern NOME_VALIDO =
            Pattern.compile("^[\\p{L}]+(?:[ '\\-][\\p{L}]+)*$");

    // ==========================================
    // IDENTIDADE E ESTADO
    // ==========================================
    private final UUID id;
    private String nome;
    private String descricao;
    private UUID categoriaPaiId; // Permite criar a árvore de categorias (nulo significa categoria raiz)
    private final List<Categoria> subcategorias; // Lista de subcategorias vinculadas
    private boolean ativa;

    private final LocalDateTime dataCadastro;
    private LocalDateTime ultimaAtualizacao;

    // ==========================================
    // CONSTRUTOR DE CRIAÇÃO (Categoria Raiz)
    // ==========================================
    public Categoria(String nome, String descricao) {
        this(nome, descricao, null);
    }

    // CONSTRUTOR DE CRIAÇÃO (Com Categoria Pai)
    public Categoria(String nome, String descricao, UUID categoriaPaiId) {
        this.id = UUID.randomUUID();

        String nomeTratado = normalizarTexto(nome);
        String descricaoTratada = normalizarTexto(descricao);

        validarNome(nomeTratado);
        validarDescricao(descricaoTratada);

        this.nome = nomeTratado;
        this.descricao = descricaoTratada;
        this.categoriaPaiId = categoriaPaiId; // Pode ser nulo se for categoria principal (Ex: "Roupas")
        this.subcategorias = new ArrayList<>();
        this.ativa = true;

        this.dataCadastro = LocalDateTime.now();
        this.ultimaAtualizacao = this.dataCadastro;
    }

    // ==========================================
    // CONSTRUTOR DE RECONSTITUIÇÃO
    // ==========================================
    public Categoria(
            UUID id,
            String nome,
            String descricao,
            UUID categoriaPaiId,
            boolean ativa,
            LocalDateTime dataCadastro,
            LocalDateTime ultimaAtualizacao) {

        this.id = Objects.requireNonNull(id, "O ID da categoria não pode ser nulo.");
        this.dataCadastro = Objects.requireNonNull(dataCadastro, "A data de cadastro não pode ser nula.");
        this.ultimaAtualizacao = Objects.requireNonNull(ultimaAtualizacao, "A data de atualização não pode ser nula.");

        String nomeTratado = normalizarTexto(nome);
        String descricaoTratada = normalizarTexto(descricao);

        validarNome(nomeTratado);
        validarDescricao(descricaoTratada);

        this.nome = nomeTratado;
        this.descricao = descricaoTratada;
        this.categoriaPaiId = categoriaPaiId;
        this.subcategorias = new ArrayList<>();
        this.ativa = ativa;
    }

    // ==========================================
    // COMPORTAMENTOS DE NEGÓCIO
    // ==========================================

    /**
     * Adiciona uma subcategoria garantindo o isolamento do domínio.
     */
    public void adicionarSubcategoria(Categoria subcategoria) {
        validarEstadoAtivo();
        Objects.requireNonNull(subcategoria, "A subcategoria não pode ser nula.");
        
        if (subcategoria.getId().equals(this.id)) {
            throw new IllegalArgumentException("Uma categoria não pode ser subcategoria de si mesma.");
        }
        
        subcategoria.categoriaPaiId = this.id;
        this.subcategorias.add(subcategoria);
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void alterarNome(String novoNome) {
        validarEstadoAtivo();
        String nomeTratado = normalizarTexto(novoNome);
        validarNome(nomeTratado);

        this.nome = nomeTratado;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void alterarDescricao(String novaDescricao) {
        validarEstadoAtivo();
        String descricaoTratada = normalizarTexto(novaDescricao);
        validarDescricao(descricaoTratada);

        this.descricao = descricaoTratada;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void ativar() {
        if (this.ativa) {
            throw new IllegalStateException("A categoria já está ativa.");
        }
        this.ativa = true;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void desativar() {
        if (!this.ativa) {
            throw new IllegalStateException("A categoria já está desativada.");
        }
        this.ativa = false;
        this.ultimaAtualizacao = LocalDateTime.now();
        
        // Regra Cascata: Se desativar a pai, desativa todas as subcategorias filhas automaticamente
        this.subcategorias.forEach(Categoria::desativar);
    }

    // ==========================================
    // GETTERS E CONSULTAS
    // ==========================================

    public UUID getId() { return this.id; }
    public String getNome() { return this.nome; }
    public String getDescricao() { return this.descricao; }
    public UUID getCategoriaPaiId() { return this.categoriaPaiId; }
    public boolean isAtiva() { return this.ativa; }
    public LocalDateTime getDataCadastro() { return this.dataCadastro; }
    public LocalDateTime getUltimaAtualizacao() { return this.ultimaAtualizacao; }
    
    /**
     * Retorna as subcategorias protegidas contra mutações diretas de fora da entidade.
     */
    public List<Categoria> getSubcategorias() {
        return Collections.unmodifiableList(subcategorias);
    }

    // ==========================================
    // VALIDAÇÕES PRIVADAS
    // ==========================================

    private void validarEstadoAtivo() {
        if (!this.ativa) {
            throw new IllegalStateException("Operação negada: categoria inativa.");
        }
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        if (nome.length() < NOME_MINIMO || nome.length() > NOME_MAXIMO) {
            throw new IllegalArgumentException("O nome deve ter entre " + NOME_MINIMO + " e " + NOME_MAXIMO + " caracteres.");
        }

        validarRegex(nome, NOME_VALIDO, "O formato do nome é inválido.");
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória.");
        }

        int tamanho = descricao.length();
        if (tamanho < DESCRICAO_MINIMA || tamanho > DESCRICAO_MAXIMA) {
            throw new IllegalArgumentException("A descrição deve ter entre " + DESCRICAO_MINIMA + " e " + DESCRICAO_MAXIMA + " caracteres.");
        }
    }

    private void validarRegex(String texto, Pattern pattern, String mensagemErro) {
        if (!pattern.matcher(texto).matches()) {
            throw new IllegalArgumentException(mensagemErro);
        }
    }

    private String normalizarTexto(String texto) {
        Objects.requireNonNull(texto, "Texto não pode ser nulo.");
        return texto.replaceAll("\\s+", " ").trim();
    }

    // ==========================================
    //             OBJECT METHODS
    // ==========================================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Categoria other = (Categoria) obj;
        if (this.id == null || other.id == null) return false;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", paiId=" + categoriaPaiId +
                ", subcategoriasContagem=" + subcategorias.size() +
                ", ativa=" + ativa +
                '}';
    }
}





