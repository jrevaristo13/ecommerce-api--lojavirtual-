package br.com.ecommerce.application.repository;

import br.com.ecommerce.domain.Carrinho;
import java.util.Optional;
import java.util.UUID;

public interface CarrinhoRepository {
    void salvar(Carrinho carrinho);
    Optional<Carrinho> buscarPorClienteId(UUID clienteId);
    void excluirPorClienteId(UUID clienteId);
    void save(Carrinho carrinho);
}
