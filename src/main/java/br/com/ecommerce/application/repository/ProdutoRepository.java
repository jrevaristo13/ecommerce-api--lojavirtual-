package br.com.ecommerce.application.repository;

import br.com.ecommerce.domain.Produto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository {
    void salvar(Produto produto);
    Optional<Produto> buscarPorId(UUID id);
    List<Produto> listarTodos();
}