package br.com.ecommerce.infrastructure.web.controller;

import br.com.ecommerce.application.repository.UsuarioRepository;
import br.com.ecommerce.domain.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping
    public ResponseEntity<String> criar(@RequestBody UsuarioRequest request) {
        String senhaCriptografada = passwordEncoder.encode(request.senha());
        Usuario usuario = new Usuario(
            request.email(),
            request.login(),
            senhaCriptografada,
            true
        );
        usuarioRepository.salvar(usuario);
        return ResponseEntity.ok("Usuário criado com sucesso!");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable UUID id) {
        return usuarioRepository.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        if (usuarioRepository.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuarioRepository.excluir(id);
        return ResponseEntity.noContent().build();
    }
    
    public record UsuarioRequest(String email, String login, String senha) {}
}
