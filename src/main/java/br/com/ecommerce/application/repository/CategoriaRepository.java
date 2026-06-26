package br.com.ecommerce.application.repository;

import br.com.ecommerce.domain.Categoria;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository {
    void salvar(Categoria categoria);
    Optional<Categoria> buscarPorId(UUID id);
    List<Categoria> listarTodas();
    List<Categoria> buscarPorCategoriaPai(UUID categoriaPaiId);
    List<Categoria> buscarRaizes();
    void excluir(UUID id);
}
