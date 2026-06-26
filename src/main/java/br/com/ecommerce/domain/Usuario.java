package br.com.ecommerce.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Usuario {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private final UUID id;
    private String login;
    private String senha;
    private String email; 
    private boolean statusAtivo;
    private final LocalDateTime dataCadastro;
    private LocalDateTime ultimaAtualizacao;

    // Construtor de criação
    public Usuario(String email, String login, String senha, boolean statusAtivo) {
        this.id = UUID.randomUUID();
        this.login = validarNome(login, "Login/Nome");
        this.senha = validarSenha(senha);
        this.email = validarFormatoEmail(email);
        this.statusAtivo = statusAtivo;
        this.dataCadastro = LocalDateTime.now();
        this.ultimaAtualizacao = this.dataCadastro;
    }

    // Construtor de reconstituição (para banco de dados)
    public Usuario(UUID id, String email, String login, String senha, boolean statusAtivo, 
                   LocalDateTime dataCadastro, LocalDateTime ultimaAtualizacao) {
        this.id = Objects.requireNonNull(id, "ID é obrigatório");
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.statusAtivo = statusAtivo;
        this.dataCadastro = dataCadastro;
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public void garantirQueEstaAtivo() {
        if (!this.statusAtivo) {
            throw new IllegalStateException("Operação negada: usuário inativo.");
        }
    }

    public void ativar() {
        this.statusAtivo = true;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void desativar() {
        this.statusAtivo = false;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void alterarSenha(String novaSenha) {
        this.senha = validarSenha(novaSenha);
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public void alterarEmail(String novoEmail) {
        this.email = validarFormatoEmail(novoEmail);
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    private String validarFormatoEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail não pode ser vazio.");
        }
        String emailSanitizado = email.trim().toLowerCase();
        if (!EMAIL_PATTERN.matcher(emailSanitizado).matches()) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        return emailSanitizado;
    }

    private String validarNome(String nome, String campo) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return normalizarTexto(nome);
    }

    private String validarSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        return senha;
    }

    private String normalizarTexto(String texto) {
        return texto.replaceAll("\\s+", " ").trim();
    }

    // Getters
    public UUID getId() { return id; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getEmail() { return email; }
    public boolean isStatusAtivo() { return statusAtivo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Usuario other)) return false; 
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", email='" + email + '\'' +
                ", statusAtivo=" + statusAtivo +
                '}';
    }
}
