package br.com.ecommerce.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class DimensaoTest {

    @Test
    @DisplayName("Deve criar dimensão com valores válidos")
    void deveCriarDimensaoComValoresValidos() {
        Dimensao d = new Dimensao(
            new BigDecimal("10.5"),
            new BigDecimal("20.3"),
            new BigDecimal("5.7"),
            new BigDecimal("0.250")
        );
        assertEquals(new BigDecimal("10.50"), d.getAltura());
        assertEquals(new BigDecimal("20.30"), d.getLargura());
        assertEquals(new BigDecimal("5.70"), d.getProfundidade());
        assertEquals(new BigDecimal("0.250"), d.getPeso());
    }

    @Test
    @DisplayName("Deve arredondar valores automaticamente")
    void deveArredondarValores() {
        Dimensao d = new Dimensao(
            new BigDecimal("10.555"),
            new BigDecimal("20.333"),
            new BigDecimal("5.777"),
            new BigDecimal("0.2555")
        );
        assertEquals(2, d.getAltura().scale());
        assertEquals(2, d.getLargura().scale());
        assertEquals(2, d.getProfundidade().scale());
        assertEquals(3, d.getPeso().scale());
    }

    @Test
    @DisplayName("Deve lançar exceção quando altura é nula")
    void deveLancarExcecaoQuandoAlturaNula() {
        assertThrows(NullPointerException.class, () ->
            new Dimensao(null, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
    }

    @Test
    @DisplayName("Deve lançar exceção quando largura é nula")
    void deveLancarExcecaoQuandoLarguraNula() {
        assertThrows(NullPointerException.class, () ->
            new Dimensao(BigDecimal.ONE, null, BigDecimal.ONE, BigDecimal.ONE));
    }

    @Test
    @DisplayName("Deve lançar exceção quando profundidade é nula")
    void deveLancarExcecaoQuandoProfundidadeNula() {
        assertThrows(NullPointerException.class, () ->
            new Dimensao(BigDecimal.ONE, BigDecimal.ONE, null, BigDecimal.ONE));
    }

    @Test
    @DisplayName("Deve lançar exceção quando peso é nulo")
    void deveLancarExcecaoQuandoPesoNulo() {
        assertThrows(NullPointerException.class, () ->
            new Dimensao(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, null));
    }

    @Test
    @DisplayName("Deve lançar exceção quando altura é zero")
    void deveLancarExcecaoQuandoAlturaZero() {
        assertThrows(IllegalArgumentException.class, () ->
            new Dimensao(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
    }

    @Test
    @DisplayName("Deve lançar exceção quando largura é negativa")
    void deveLancarExcecaoQuandoLarguraNegativa() {
        assertThrows(IllegalArgumentException.class, () ->
            new Dimensao(BigDecimal.ONE, new BigDecimal("-5"), BigDecimal.ONE, BigDecimal.ONE));
    }

    @Test
    @DisplayName("Deve lançar exceção quando profundidade é zero")
    void deveLancarExcecaoQuandoProfundidadeZero() {
        assertThrows(IllegalArgumentException.class, () ->
            new Dimensao(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ONE));
    }

    @Test
    @DisplayName("Deve lançar exceção quando peso é negativo")
    void deveLancarExcecaoQuandoPesoNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
            new Dimensao(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, new BigDecimal("-0.5")));
    }
}
