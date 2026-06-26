package br.com.ecommerce.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
public final class Dimensao {
    private static final BigDecimal LIMITE_MINIMO = BigDecimal.ZERO;
    private static final RoundingMode ARREDONDAMENTO = RoundingMode.HALF_EVEN;

    private BigDecimal altura;
    private BigDecimal largura;
    private BigDecimal profundidade;
    private BigDecimal peso;

    // Construtor padrão exigido pelo JPA (para @Embeddable)
    public Dimensao() {}

    @JsonCreator
    public Dimensao(
            @JsonProperty("altura") BigDecimal altura,
            @JsonProperty("largura") BigDecimal largura,
            @JsonProperty("profundidade") BigDecimal profundidade,
            @JsonProperty("peso") BigDecimal peso) {
        
        Objects.requireNonNull(altura, "A altura não pode ser nula.");
        Objects.requireNonNull(largura, "A largura não pode ser nula.");
        Objects.requireNonNull(profundidade, "A profundidade não pode ser nula.");
        Objects.requireNonNull(peso, "O peso não pode ser nulo.");

        if (altura.compareTo(LIMITE_MINIMO) <= 0) throw new IllegalArgumentException("A altura deve ser maior que zero.");
        if (largura.compareTo(LIMITE_MINIMO) <= 0) throw new IllegalArgumentException("A largura deve ser maior que zero.");
        if (profundidade.compareTo(LIMITE_MINIMO) <= 0) throw new IllegalArgumentException("A profundidade deve ser maior que zero.");
        if (peso.compareTo(LIMITE_MINIMO) <= 0) throw new IllegalArgumentException("O peso deve ser maior que zero.");

        this.altura = altura.setScale(2, ARREDONDAMENTO);
        this.largura = largura.setScale(2, ARREDONDAMENTO);
        this.profundidade = profundidade.setScale(2, ARREDONDAMENTO);
        this.peso = peso.setScale(3, ARREDONDAMENTO);
    }

    public BigDecimal getAltura() { return altura; }
    public BigDecimal getLargura() { return largura; }
    public BigDecimal getProfundidade() { return profundidade; }
    public BigDecimal getPeso() { return peso; }
}
