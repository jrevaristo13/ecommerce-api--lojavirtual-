package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.repository.PagamentoRepository;
import br.com.ecommerce.application.repository.PedidoRepository;
import br.com.ecommerce.domain.FormaPagamento;
import br.com.ecommerce.domain.Pagamento;
import br.com.ecommerce.domain.Pedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pagamentos")
@Tag(name = "Pagamentos", description = "Endpoints para processamento de pagamentos")
public class PagamentoController {
    
    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    
    public PagamentoController(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }
    
    @PostMapping
    @Operation(summary = "Criar pagamento", description = "Inicia um novo pagamento para um pedido")
    @ApiResponse(responseCode = "200", description = "Pagamento criado com sucesso")
    @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    public ResponseEntity<String> criar(@RequestBody PagamentoRequest request) {
        var pedidoOpt = pedidoRepository.findById(request.pedidoId());
        if (pedidoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Pedido pedido = pedidoOpt.get();
        Pagamento pagamento = new Pagamento(
            request.pedidoId(),
            pedido.getValorTotal(),
            request.formaPagamento()
        );
        
        pagamentoRepository.salvar(pagamento);
        return ResponseEntity.ok("Pagamento criado com sucesso! ID: " + pagamento.getId());
    }
    
    @GetMapping
    @Operation(summary = "Listar pagamentos", description = "Retorna todos os pagamentos")
    public ResponseEntity<List<Pagamento>> listar() {
        return ResponseEntity.ok(pagamentoRepository.listarTodos());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID", description = "Retorna um pagamento específico")
    @ApiResponse(responseCode = "200", description = "Pagamento encontrado")
    @ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable UUID id) {
        return pagamentoRepository.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Buscar pagamento por pedido", description = "Retorna o pagamento de um pedido específico")
    public ResponseEntity<Pagamento> buscarPorPedidoId(@PathVariable UUID pedidoId) {
        return pagamentoRepository.buscarPorPedidoId(pedidoId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/aprovar")
    @Operation(summary = "Aprovar pagamento", description = "Aprova um pagamento pendente e atualiza o status do pedido para PAGO")
    @ApiResponse(responseCode = "200", description = "Pagamento aprovado e pedido atualizado")
    @ApiResponse(responseCode = "400", description = "Pagamento não pode ser aprovado")
    public ResponseEntity<String> aprovar(@PathVariable UUID id) {
        var pagamentoOpt = pagamentoRepository.buscarPorId(id);
        if (pagamentoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Pagamento pagamento = pagamentoOpt.get();
        pagamento.aprovar();
        pagamentoRepository.salvar(pagamento);
        
        // ⭐ ATUALIZAR STATUS DO PEDIDO
        var pedidoOpt = pedidoRepository.findById(pagamento.getPedidoId());
        if (pedidoOpt.isPresent()) {
            Pedido pedido = pedidoOpt.get();
            pedido.marcarComoPago();
            pedidoRepository.save(pedido);
        }
        
        return ResponseEntity.ok("Pagamento aprovado com sucesso! Pedido atualizado para PAGO.");
    }
    
    @PostMapping("/{id}/recusar")
    @Operation(summary = "Recusar pagamento", description = "Recusa um pagamento pendente")
    public ResponseEntity<String> recusar(@PathVariable UUID id) {
        var pagamentoOpt = pagamentoRepository.buscarPorId(id);
        if (pagamentoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Pagamento pagamento = pagamentoOpt.get();
        pagamento.recusar();
        pagamentoRepository.salvar(pagamento);
        
        return ResponseEntity.ok("Pagamento recusado com sucesso!");
    }
    
    @PostMapping("/{id}/estornar")
    @Operation(summary = "Estornar pagamento", description = "Estorna um pagamento aprovado")
    public ResponseEntity<String> estornar(@PathVariable UUID id) {
        var pagamentoOpt = pagamentoRepository.buscarPorId(id);
        if (pagamentoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Pagamento pagamento = pagamentoOpt.get();
        pagamento.estornar();
        pagamentoRepository.salvar(pagamento);
        
        return ResponseEntity.ok("Pagamento estornado com sucesso!");
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pagamento", description = "Remove um pagamento do sistema")
    @ApiResponse(responseCode = "204", description = "Pagamento excluído")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        if (pagamentoRepository.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        pagamentoRepository.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
    public record PagamentoRequest(UUID pedidoId, FormaPagamento formaPagamento) {}
}
