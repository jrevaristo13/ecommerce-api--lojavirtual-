package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.repository.MarcaRepository;
import br.com.ecommerce.domain.Marca;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/marcas")
public class MarcaController {
    
    private final MarcaRepository marcaRepository;
    
    public MarcaController(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }
    
    @PostMapping
    public ResponseEntity<String> criar(@RequestBody MarcaRequest request) {
        Marca marca = new Marca(
            request.nome(),
            request.descricao(),
            request.siteOficial(),
            request.emailContato(),
            request.telefoneContato(),
            request.logoUrl(),
            true
        );
        marcaRepository.salvar(marca);
        return ResponseEntity.ok("Marca criada com sucesso!");
    }
    
    @GetMapping
    public ResponseEntity<List<Marca>> listar() {
        return ResponseEntity.ok(marcaRepository.listarTodas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Marca> buscarPorId(@PathVariable UUID id) {
        return marcaRepository.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        if (marcaRepository.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        marcaRepository.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
    public record MarcaRequest(
        String nome,
        String descricao,
        String siteOficial,
        String emailContato,
        String telefoneContato,
        String logoUrl
    ) {}
}
