package br.com.ecommerce.application.repository;

import br.com.ecommerce.domain.Pedido;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository {
    void save(Pedido pedido); // Mantemos apenas um padrão
    Optional<Pedido> findById(UUID id); // Opcional: findById é o padrão do Spring Data
}