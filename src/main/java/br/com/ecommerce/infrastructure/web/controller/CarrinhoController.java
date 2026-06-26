package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.dto.AdicionarItemDTO;
import br.com.ecommerce.application.dto.AdicionarItemInput;
import br.com.ecommerce.application.usecase.AdicionarItemAoCarrinhoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrinho")
public class CarrinhoController {

    private final AdicionarItemAoCarrinhoUseCase adicionarItemUseCase;

    public CarrinhoController(AdicionarItemAoCarrinhoUseCase adicionarItemUseCase) {
        this.adicionarItemUseCase = adicionarItemUseCase;
    }

   @PostMapping("/adicionar")
public ResponseEntity<?> adicionarItem(@RequestBody AdicionarItemDTO dto) {
    try {
        AdicionarItemInput input = new AdicionarItemInput(dto.clienteId(), dto.skuId(), dto.quantidade());
        adicionarItemUseCase.executar(input);
        return ResponseEntity.ok("Item adicionado com sucesso!");
    } catch (Exception e) {
        // Isso vai imprimir o rastreio completo no seu terminal (onde o mvn spring-boot:run está rodando)
        e.printStackTrace(); 
        return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
    }
}
}