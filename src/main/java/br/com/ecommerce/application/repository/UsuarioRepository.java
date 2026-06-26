package br.com.ecommerce.application.repository;

import br.com.ecommerce.domain.Usuario;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {
    void salvar(Usuario usuario);
    Optional<Usuario> buscarPorId(UUID id);
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorLogin(String login);
    void excluir(UUID id);
}
