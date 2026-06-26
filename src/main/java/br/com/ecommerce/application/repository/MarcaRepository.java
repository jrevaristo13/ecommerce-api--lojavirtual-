package br.com.ecommerce.application.repository;

import br.com.ecommerce.domain.Marca;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarcaRepository {
    void salvar(Marca marca);
    Optional<Marca> buscarPorId(UUID id);
    List<Marca> listarTodas();
    Optional<Marca> buscarPorNome(String nome);
    void excluir(UUID id);
}
