package br.com.ecommerce.domain;

import br.com.ecommerce.domain.TipoDescontos.TipoDesconto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CupomTest {

    private Cupom cupomPercentual;
    private Cupom cupomValorFixo;

    @BeforeEach
    void setUp() {
        cupomPercentual = criarCupomPercentual("DESCONTO10", "10% de desconto", new BigDecimal("10.00"), new BigDecimal("100.00"), 100, 0, true);
        cupomValorFixo = criarCupomValorFixo("FIXO50", "50 reais de desconto", new BigDecimal("50.00"), new BigDecimal("200.00"), 50, 0, true);
    }

    private Cupom criarCupomPercentual(String codigo, String descricao, BigDecimal valorDesconto, 
                                        BigDecimal valorMinimo, int limiteUso, int qtdUtilizada, boolean ativo) {
        return new Cupom(
            codigo,
            descricao,
            TipoDesconto.PERCENTUAL,
            valorDesconto,
            valorMinimo,
            limiteUso,
            qtdUtilizada,
            ativo,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(30)
        );
    }

    private Cupom criarCupomValorFixo(String codigo, String descricao, BigDecimal valorDesconto, 
                                       BigDecimal valorMinimo, int limiteUso, int qtdUtilizada, boolean ativo) {
        return new Cupom(
            codigo,
            descricao,
            TipoDesconto.VALOR_FIXO,
            valorDesconto,
            valorMinimo,
            limiteUso,
            qtdUtilizada,
            ativo,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(30)
        );
    }

    // ========================================
    // TESTES DE CRIAÇÃO E VALIDAÇÕES
    // ========================================
    @Nested
    @DisplayName("Criação e Validações")
    class CriacaoTest {

        @Test
        @DisplayName("Deve criar cupom percentual válido")
        void deveCriarCupomPercentualValido() {
            assertNotNull(cupomPercentual.getId());
            assertEquals("DESCONTO10", cupomPercentual.getCodigo());
            assertEquals(TipoDesconto.PERCENTUAL, cupomPercentual.getTipoDesconto());
            assertEquals(new BigDecimal("10.00"), cupomPercentual.getValorDesconto());
            assertTrue(cupomPercentual.isAtivo());
        }

        @Test
        @DisplayName("Deve criar cupom valor fixo válido")
        void deveCriarCupomValorFixoValido() {
            assertNotNull(cupomValorFixo.getId());
            assertEquals("FIXO50", cupomValorFixo.getCodigo());
            assertEquals(TipoDesconto.VALOR_FIXO, cupomValorFixo.getTipoDesconto());
            assertEquals(new BigDecimal("50.00"), cupomValorFixo.getValorDesconto());
        }

        @Test
        @DisplayName("Deve normalizar código para maiúsculas")
        void deveNormalizarCodigoParaMaiusculas() {
            Cupom cupom = criarCupomPercentual("codigo123", "Descrição válida", new BigDecimal("10.00"), new BigDecimal("100.00"), 10, 0, true);
            assertEquals("CODIGO123", cupom.getCodigo());
        }

        @Test
        @DisplayName("Deve lançar exceção quando código é nulo")
        void deveLancarExcecaoQuandoCodigoNulo() {
            assertThrows(NullPointerException.class, () ->
                criarCupomPercentual(null, "Descrição", new BigDecimal("10.00"), new BigDecimal("100.00"), 10, 0, true));
        }

        @Test
        @DisplayName("Deve lançar exceção quando código tem menos de 5 caracteres")
        void deveLancarExcecaoQuandoCodigoMuitoCurto() {
            assertThrows(IllegalArgumentException.class, () ->
                criarCupomPercentual("ABC", "Descrição", new BigDecimal("10.00"), new BigDecimal("100.00"), 10, 0, true));
        }

        @Test
        @DisplayName("Deve lançar exceção quando código tem caracteres inválidos")
        void deveLancarExcecaoQuandoCodigoComCaracteresInvalidos() {
            assertThrows(IllegalArgumentException.class, () ->
                criarCupomPercentual("CODIGO-INVALIDO", "Descrição", new BigDecimal("10.00"), new BigDecimal("100.00"), 10, 0, true));
        }

        @Test
        @DisplayName("Deve lançar exceção quando descrição é nula")
        void deveLancarExcecaoQuandoDescricaoNula() {
            assertThrows(NullPointerException.class, () ->
                criarCupomPercentual("CODIGO123", null, new BigDecimal("10.00"), new BigDecimal("100.00"), 10, 0, true));
        }

        @Test
        @DisplayName("Deve lançar exceção quando percentual está fora do range")
        void deveLancarExcecaoQuandoPercentualForaDoRange() {
            assertThrows(IllegalArgumentException.class, () ->
                criarCupomPercentual("CODIGO123", "Descrição", new BigDecimal("101.00"), new BigDecimal("100.00"), 10, 0, true));
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor fixo é maior que valor mínimo")
        void deveLancarExcecaoQuandoValorFixoMaiorQueMinimo() {
            assertThrows(IllegalArgumentException.class, () ->
                criarCupomValorFixo("CODIGO123", "Descrição", new BigDecimal("300.00"), new BigDecimal("200.00"), 10, 0, true));
        }

        @Test
        @DisplayName("Deve lançar exceção quando data início é depois da data fim")
        void deveLancarExcecaoQuandoDataInicioDepoisDataFim() {
            assertThrows(IllegalArgumentException.class, () ->
                new Cupom("CODIGO123", "Descrição", TipoDesconto.PERCENTUAL, new BigDecimal("10.00"), 
                    new BigDecimal("100.00"), 10, 0, true,
                    LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(5)));
        }
    }

    // ========================================
    // TESTES DE CÁLCULO DE DESCONTO
    // ========================================
    @Nested
    @DisplayName("Cálculo de Desconto")
    class CalculoDescontoTest {

        @Test
        @DisplayName("Deve calcular desconto percentual corretamente")
        void deveCalcularDescontoPercentualCorretamente() {
            BigDecimal desconto = cupomPercentual.calcularDesconto(new BigDecimal("500.00"));
            assertEquals(new BigDecimal("50.00"), desconto); // 10% de 500 = 50
        }

        @Test
        @DisplayName("Deve calcular desconto fixo corretamente")
        void deveCalcularDescontoFixoCorretamente() {
            BigDecimal desconto = cupomValorFixo.calcularDesconto(new BigDecimal("500.00"));
            assertEquals(new BigDecimal("50.00"), desconto);
        }

        @Test
        @DisplayName("Deve retornar zero quando valor do pedido é menor que mínimo")
        void deveRetornarZeroQuandoPedidoMenorQueMinimo() {
            BigDecimal desconto = cupomPercentual.calcularDesconto(new BigDecimal("50.00"));
            assertEquals(BigDecimal.ZERO.setScale(2), desconto); // Mínimo é 100
        }

        @Test
        @DisplayName("Deve retornar zero quando cupom não pode ser utilizado")
        void deveRetornarZeroQuandoCupomNaoPodeSerUtilizado() {
            cupomPercentual.desativar();
            BigDecimal desconto = cupomPercentual.calcularDesconto(new BigDecimal("500.00"));
            assertEquals(BigDecimal.ZERO.setScale(2), desconto);
        }

        @Test
        @DisplayName("Deve lançar exceção quando valor do pedido é nulo")
        void deveLancarExcecaoQuandoValorPedidoNulo() {
            assertThrows(IllegalArgumentException.class, () ->
                cupomPercentual.calcularDesconto(null));
        }
    }

    // ========================================
    // TESTES DE CONTROLE DE UTILIZAÇÃO
    // ========================================
    @Nested
    @DisplayName("Controle de Utilização")
    class ControleUtilizacaoTest {

        @Test
        @DisplayName("Deve registrar utilização com sucesso")
        void deveRegistrarUtilizacaoComSucesso() {
            int qtdAntes = cupomPercentual.getQuantidadeUtilizada();
            cupomPercentual.registrarUtilizacao();
            assertEquals(qtdAntes + 1, cupomPercentual.getQuantidadeUtilizada());
        }

        @Test
        @DisplayName("Deve lançar exceção ao registrar utilização quando atingiu limite")
        void deveLancarExcecaoAoRegistrarQuandoAtingiuLimite() {
            Cupom cupomLimite = criarCupomPercentual("LIMITE1", "Cupom limite", new BigDecimal("10.00"), new BigDecimal("100.00"), 1, 1, true);
            assertThrows(IllegalStateException.class, () -> cupomLimite.registrarUtilizacao());
        }

        @Test
        @DisplayName("Deve cancelar utilização com sucesso")
        void deveCancelarUtilizacaoComSucesso() {
            cupomPercentual.registrarUtilizacao();
            int qtdAntes = cupomPercentual.getQuantidadeUtilizada();
            cupomPercentual.cancelarUtilizacao();
            assertEquals(qtdAntes - 1, cupomPercentual.getQuantidadeUtilizada());
        }

        @Test
        @DisplayName("Deve lançar exceção ao cancelar utilização quando não há utilizações")
        void deveLancarExcecaoAoCancelarQuandoNaoHaUtilizacoes() {
            assertThrows(IllegalStateException.class, () -> cupomPercentual.cancelarUtilizacao());
        }

        @Test
        @DisplayName("Deve verificar se atingiu limite de uso")
        void deveVerificarSeAtingiuLimiteDeUso() {
            assertFalse(cupomPercentual.atingiuLimiteUso());
            Cupom cupomLimite = criarCupomPercentual("LIMITE1", "Cupom limite", new BigDecimal("10.00"), new BigDecimal("100.00"), 1, 1, true);
            assertTrue(cupomLimite.atingiuLimiteUso());
        }
    }

    // ========================================
    // TESTES DE STATUS
    // ========================================
    @Nested
    @DisplayName("Status do Cupom")
    class StatusTest {

        @Test
        @DisplayName("Deve verificar se cupom está ativo")
        void deveVerificarSeCupomEstaAtivo() {
            assertTrue(cupomPercentual.isAtivo());
            cupomPercentual.desativar();
            assertFalse(cupomPercentual.isAtivo());
        }

        @Test
        @DisplayName("Deve ativar cupom inativo")
        void deveAtivarCupomInativo() {
            cupomPercentual.desativar();
            assertFalse(cupomPercentual.isAtivo());
            cupomPercentual.ativar();
            assertTrue(cupomPercentual.isAtivo());
        }

        @Test
        @DisplayName("Deve verificar se cupom está vigente")
        void deveVerificarSeCupomEstaVigente() {
            assertTrue(cupomPercentual.estaVigente());
        }

        @Test
        @DisplayName("Deve verificar se cupom está expirado")
        void deveVerificarSeCupomEstaExpirado() {
            Cupom cupomExpirado = new Cupom("EXPIRADO", "Cupom expirado", TipoDesconto.PERCENTUAL, 
                new BigDecimal("10.00"), new BigDecimal("100.00"), 10, 0, true,
                LocalDateTime.now().minusDays(30), LocalDateTime.now().minusDays(1));
            assertTrue(cupomExpirado.estaExpirado());
        }

        @Test
        @DisplayName("Deve verificar se cupom pode ser utilizado")
        void deveVerificarSeCupomPodeSerUtilizado() {
            assertTrue(cupomPercentual.podeSerUtilizado());
            cupomPercentual.desativar();
            assertFalse(cupomPercentual.podeSerUtilizado());
        }
    }

    // ========================================
    // TESTES DE APLICAÇÃO DE DESCONTO
    // ========================================
    @Nested
    @DisplayName("Aplicação de Desconto")
    class AplicacaoDescontoTest {

        @Test
        @DisplayName("Deve aplicar desconto percentual ao valor do pedido")
        void deveAplicarDescontoPercentualAoValorDoPedido() {
            BigDecimal valorFinal = cupomPercentual.aplicarDesconto(new BigDecimal("500.00"));
            assertEquals(new BigDecimal("450.00"), valorFinal); // 500 - 50 (10%)
        }

        @Test
        @DisplayName("Deve aplicar desconto fixo ao valor do pedido")
        void deveAplicarDescontoFixoAoValorDoPedido() {
            BigDecimal valorFinal = cupomValorFixo.aplicarDesconto(new BigDecimal("500.00"));
            assertEquals(new BigDecimal("450.00"), valorFinal); // 500 - 50
        }

        @Test
        @DisplayName("Deve retornar valor original quando cupom não pode ser utilizado")
        void deveRetornarValorOriginalQuandoCupomNaoPodeSerUtilizado() {
            cupomPercentual.desativar();
            BigDecimal valorFinal = cupomPercentual.aplicarDesconto(new BigDecimal("500.00"));
            assertEquals(new BigDecimal("500.00"), valorFinal);
        }
    }
}
