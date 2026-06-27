package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.dto.SkuRequest;
import br.com.ecommerce.application.usecase.*;
import br.com.ecommerce.domain.Sku;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/skus")
public class SkuController {

    private final CriarSkuUseCase criarSkuUseCase;
    private final BuscarSkuUseCase buscarSkuUseCase;
    private final ListarSkusUseCase listarSkusUseCase; // Alterado para ListarSkus
    private final AtualizarEstoqueUseCase atualizarEstoqueUseCase;
    private final DesativarSkuUseCase desativarSkuUseCase;

    public SkuController(CriarSkuUseCase criarSkuUseCase, 
                         BuscarSkuUseCase buscarSkuUseCase, 
                         ListarSkusUseCase listarSkusUseCase,
                         AtualizarEstoqueUseCase atualizarEstoqueUseCase,
                         DesativarSkuUseCase desativarSkuUseCase) {
        this.criarSkuUseCase = criarSkuUseCase;
        this.buscarSkuUseCase = buscarSkuUseCase;
        this.listarSkusUseCase = listarSkusUseCase;
        this.atualizarEstoqueUseCase = atualizarEstoqueUseCase;
        this.desativarSkuUseCase = desativarSkuUseCase;
    }

    @PostMapping
    public ResponseEntity<String> criar(@Valid @RequestBody SkuRequest request) { 
       Sku sku = new Sku(
    UUID.randomUUID(), 
    request.produtoId(),
    request.codigoSku(),
    request.nomeVariacao(),
    request.preco(),
    request.quantidadeEstoque(),
    true, // <--- ADICIONE ESTE PARÂMETRO
    request.dimensao() != null ? request.dimensao().toDimensao() : null
);
        
        criarSkuUseCase.executar(sku);
        return ResponseEntity.ok("SKU criado com sucesso!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sku> buscarPorId(@PathVariable UUID id) {
        return buscarSkuUseCase.executar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Sku>> listarTodos() {
        // Agora retornamos a lista de SKUs que estão no banco
        return ResponseEntity.ok(listarSkusUseCase.executar());
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<Void> atualizarEstoque(@PathVariable UUID id, @RequestBody Integer novaQuantidade) {
        if (novaQuantidade == null) return ResponseEntity.badRequest().build();
        atualizarEstoqueUseCase.executar(id, novaQuantidade);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable UUID id) {
        desativarSkuUseCase.executar(id);
        return ResponseEntity.noContent().build();
    }
}
