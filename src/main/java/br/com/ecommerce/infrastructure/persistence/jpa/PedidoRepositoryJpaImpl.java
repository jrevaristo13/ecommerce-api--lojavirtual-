package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.application.repository.PedidoRepository;
import br.com.ecommerce.domain.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PedidoRepositoryJpaImpl implements PedidoRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoRepositoryJpaImpl.class);
    
    private final PedidoRepositoryJpa pedidoRepositoryJpa;
    private final PedidoMapper pedidoMapper;
    
    public PedidoRepositoryJpaImpl(@Lazy PedidoRepositoryJpa pedidoRepositoryJpa, PedidoMapper pedidoMapper) {
        this.pedidoRepositoryJpa = pedidoRepositoryJpa;
        this.pedidoMapper = pedidoMapper;
    }
    
    @Override
    @Transactional
    public void save(Pedido pedido) {
        logger.info("Salvando pedido: {}", pedido.getId());
        PedidoEntity entity = pedidoMapper.toEntity(pedido);
        pedidoRepositoryJpa.save(entity);
        logger.info("Pedido salvo com sucesso!");
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findById(UUID id) {
        logger.info("Buscando pedido com itens: {}", id);
        return pedidoRepositoryJpa.findByIdComItens(id)
            .map(pedidoMapper::toDomain);
    }
}
