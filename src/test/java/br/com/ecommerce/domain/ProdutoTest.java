package br.com.ecommerce.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoTest {

    private Produto criarProdutoValido() {
        return new Produto(
            UUID.randomUUID(),
            "Camiseta Premium",
            UUID.randomUUID(),
            true
        );
    }

    private Sku criarSkuValido(String codigo) {
        return new Sku(
            UUID.randomUUID(),
            UUID.randomUUID(),
            codigo,
            "Variação Teste",
            new BigDecimal("99.90"),
            10,
            true,
            new Dimensao(
                new BigDecimal("2"),
                new BigDecimal("30"),
                new BigDecimal("20"),
                new BigDecimal("0.250")
            )
        );
    }

    @Nested
    @DisplayName("Construtor do Produto")
    class ConstrutorTest {

        @Test
        @DisplayName("Deve criar produto com dados válidos")
        void deveCriarProdutoComDadosValidos() {
            UUID id = UUID.randomUUID();
            UUID marcaId = UUID.randomUUID();
            Produto produto = new Produto(id, "Camiseta", marcaId, true);

            assertEquals(id, produto.getId());
            assertEquals("Camiseta", produto.getNome());
            assertEquals(marcaId, produto.getMarcaId());
            assertTrue(produto.isAtivo());
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando ID é nulo")
        void deveLancarExcecaoQuandoIdNulo() {
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Produto(null, "Camiseta", UUID.randomUUID(), true)
            );
            assertEquals("ID é obrigatório", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando nome é nulo")
        void deveLancarExcecaoQuandoNomeNulo() {
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Produto(UUID.randomUUID(), null, UUID.randomUUID(), true)
            );
            assertEquals("Nome é obrigatório", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando marcaId é nulo")
        void deveLancarExcecaoQuandoMarcaIdNulo() {
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Produto(UUID.randomUUID(), "Camiseta", null, true)
            );
            assertEquals("MarcaID é obrigatório", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Método adicionarSku")
    class AdicionarSkuTest {

        private Produto produto;

        @BeforeEach
        void setUp() {
            produto = criarProdutoValido();
        }

        @Test
        @DisplayName("Deve adicionar SKU com sucesso quando código é único")
        void deveAdicionarSkuComSucesso() {
            Sku sku = criarSkuValido("SKU-001");
            produto.adicionarSku(sku);
            assertEquals(1, produto.getSkus().size());
            assertTrue(produto.getSkus().contains(sku));
        }

        @Test
        @DisplayName("Deve adicionar múltiplos SKUs com códigos diferentes")
        void deveAdicionarMultiplosSkus() {
            produto.adicionarSku(criarSkuValido("SKU-001"));
            produto.adicionarSku(criarSkuValido("SKU-002"));
            produto.adicionarSku(criarSkuValido("SKU-003"));
            assertEquals(3, produto.getSkus().size());
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando SKU é nulo")
        void deveLancarExcecaoQuandoSkuNulo() {
            NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> produto.adicionarSku(null)
            );
            assertEquals("O SKU não pode ser nulo.", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar IllegalArgumentException quando código SKU já existe")
        void deveLancarExcecaoQuandoCodigoDuplicado() {
            Sku sku1 = criarSkuValido("SKU-DUPLICADO");
            Sku sku2 = criarSkuValido("SKU-DUPLICADO");
            produto.adicionarSku(sku1);

            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> produto.adicionarSku(sku2)
            );
            assertTrue(exception.getMessage().contains("Já existe um SKU com o código"));
        }

        @Test
        @DisplayName("Deve considerar códigos duplicados independente de maiúsculas/minúsculas")
        void deveConsiderarDuplicadoIndependenteDeCase() {
            produto.adicionarSku(criarSkuValido("sku-teste"));
            assertThrows(
                IllegalArgumentException.class,
                () -> produto.adicionarSku(criarSkuValido("SKU-TESTE"))
            );
        }
    }

    @Nested
    @DisplayName("Método getSkuPorId")
    class GetSkuPorIdTest {

        private Produto produto;
        private Sku sku1;
        private Sku sku2;

        @BeforeEach
        void setUp() {
            produto = criarProdutoValido();
            sku1 = criarSkuValido("SKU-001");
            sku2 = criarSkuValido("SKU-002");
            produto.adicionarSku(sku1);
            produto.adicionarSku(sku2);
        }

        @Test
        @DisplayName("Deve retornar Optional com SKU quando ID existe")
        void deveRetornarSkuQuandoIdExiste() {
            var resultado = produto.getSkuPorId(sku1.getId());
            assertTrue(resultado.isPresent());
            assertEquals(sku1, resultado.get());
        }

        @Test
        @DisplayName("Deve retornar Optional vazio quando ID não existe")
        void deveRetornarOptionalVazioQuandoIdNaoExiste() {
            var resultado = produto.getSkuPorId(UUID.randomUUID());
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve encontrar SKU correto entre múltiplos SKUs")
        void deveEncontrarSkuCorretoEntreMultiplos() {
            var resultado = produto.getSkuPorId(sku2.getId());
            assertTrue(resultado.isPresent());
            assertEquals(sku2, resultado.get());
        }
    }

    @Nested
    @DisplayName("Método getSkus - Imutabilidade")
    class GetSkusTest {

        @Test
        @DisplayName("Deve retornar lista vazia quando nenhum SKU foi adicionado")
        void deveRetornarListaVaziaQuandoNenhumSku() {
            Produto produto = criarProdutoValido();
            assertTrue(produto.getSkus().isEmpty());
        }

        @Test
        @DisplayName("Deve retornar lista com SKUs adicionados")
        void deveRetornarListaComSkus() {
            Produto produto = criarProdutoValido();
            Sku sku1 = criarSkuValido("SKU-001");
            Sku sku2 = criarSkuValido("SKU-002");
            produto.adicionarSku(sku1);
            produto.adicionarSku(sku2);

            var skus = produto.getSkus();
            assertEquals(2, skus.size());
            assertTrue(skus.contains(sku1));
            assertTrue(skus.contains(sku2));
        }

        @Test
        @DisplayName("Deve retornar lista imutável - não permite adicionar")
        void deveRetornarListaImutavel_NaoPermiteAdicionar() {
            Produto produto = criarProdutoValido();
            var skus = produto.getSkus();
            Sku novoSku = criarSkuValido("SKU-NOVO");

            assertThrows(
                UnsupportedOperationException.class,
                () -> skus.add(novoSku)
            );
        }

        @Test
        @DisplayName("Deve retornar lista imutável - não permite remover")
        void deveRetornarListaImutavel_NaoPermiteRemover() {
            Produto produto = criarProdutoValido();
            Sku sku = criarSkuValido("SKU-001");
            produto.adicionarSku(sku);
            var skus = produto.getSkus();

            assertThrows(
                UnsupportedOperationException.class,
                () -> skus.remove(sku)
            );
        }

        @Test
        @DisplayName("Deve retornar lista imutável - não permite limpar")
        void deveRetornarListaImutavel_NaoPermiteLimpar() {
            Produto produto = criarProdutoValido();
            var skus = produto.getSkus();

            assertThrows(
                UnsupportedOperationException.class,
                () -> skus.clear()
            );
        }
    }
}
