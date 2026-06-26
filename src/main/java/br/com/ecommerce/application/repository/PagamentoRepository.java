package br.com.ecommerce.application.repository;

import br.com.ecommerce.domain.Pagamento;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PagamentoRepository {
    void salvar(Pagamento pagamento);
    Optional<Pagamento> buscarPorId(UUID id);
    Optional<Pagamento> buscarPorPedidoId(UUID pedidoId);
    List<Pagamento> listarTodos();
    void excluir(UUID id);
}
