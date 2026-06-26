package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.dto.ItemCompraDTO;
import br.com.ecommerce.application.usecase.CriarPedidoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final CriarPedidoUseCase criarPedidoUseCase;

    public PedidoController(CriarPedidoUseCase criarPedidoUseCase) {
        this.criarPedidoUseCase = criarPedidoUseCase;
    }

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody PedidoRequest request) {
        criarPedidoUseCase.executar(request.cliente(), request.itens());
        return ResponseEntity.ok(Map.of("mensagem", "Pedido criado com sucesso!"));
    }
}

// Record para receber o JSON do front
record PedidoRequest(String cliente, List<ItemCompraDTO> itens) {}
