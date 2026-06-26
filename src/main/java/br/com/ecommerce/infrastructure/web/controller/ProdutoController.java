package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.usecase.ListarProdutosUseCase;
import br.com.ecommerce.application.repository.ProdutoRepository;
import br.com.ecommerce.domain.Produto;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);
    
    private final ListarProdutosUseCase listarProdutosUseCase;
    private final ProdutoRepository produtoRepository;

    public ProdutoController(ListarProdutosUseCase listarProdutosUseCase, 
                             ProdutoRepository produtoRepository) {
        this.listarProdutosUseCase = listarProdutosUseCase;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos(
            @RequestParam(defaultValue = "false") boolean apenasAtivos) {
        return ResponseEntity.ok(listarProdutosUseCase.executar(apenasAtivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable UUID id) {
        return produtoRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> criar(@RequestBody ProdutoRequest request) {
        logger.info("Recebendo requisição - Nome: {}, MarcaId: {}", request.nome(), request.marcaId());
        
        // Validação
        if (request.nome() == null || request.nome().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Nome do produto é obrigatório");
        }
        if (request.marcaId() == null || request.marcaId().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("MarcaId é obrigatório");
        }
        
        UUID marcaId;
        try {
            marcaId = UUID.fromString(request.marcaId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("MarcaId inválido. Use formato UUID válido.");
        }
        
        logger.info("Criando produto com marcaId: {}", marcaId);
        
        Produto produto = new Produto(
                UUID.randomUUID(),
                request.nome(),
                marcaId,
                true
        );
        
        produtoRepository.salvar(produto);
        return ResponseEntity.ok("Produto criado com sucesso!");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable UUID id) {
        produtoRepository.buscarPorId(id).ifPresent(produto -> {
            Produto produtoInativo = new Produto(
                    produto.getId(),
                    produto.getNome(),
                    produto.getMarcaId(),
                    false
            );
            produtoRepository.salvar(produtoInativo);
        });
        return ResponseEntity.noContent().build();
    }
    
    // DTO com @JsonProperty para garantir mapeamento correto
    public record ProdutoRequest(
        @JsonProperty("nome") String nome,
        @JsonProperty("marcaId") String marcaId
    ) {}
}
