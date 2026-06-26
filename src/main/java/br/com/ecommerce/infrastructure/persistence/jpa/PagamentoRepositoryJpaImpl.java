package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.application.repository.PagamentoRepository;
import br.com.ecommerce.domain.Pagamento;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PagamentoRepositoryJpaImpl implements PagamentoRepository {
    
    private final PagamentoRepositoryJpa pagamentoRepositoryJpa;
    private final PagamentoMapper pagamentoMapper;
    
    public PagamentoRepositoryJpaImpl(@Lazy PagamentoRepositoryJpa pagamentoRepositoryJpa, PagamentoMapper pagamentoMapper) {
        this.pagamentoRepositoryJpa = pagamentoRepositoryJpa;
        this.pagamentoMapper = pagamentoMapper;
    }
    
    @Override
    @Transactional
    public void salvar(Pagamento pagamento) {
        PagamentoEntity entity = pagamentoMapper.toEntity(pagamento);
        pagamentoRepositoryJpa.save(entity);
    }
    
    @Override
    public Optional<Pagamento> buscarPorId(UUID id) {
        return pagamentoRepositoryJpa.findById(id).map(pagamentoMapper::toDomain);
    }
    
    @Override
    public Optional<Pagamento> buscarPorPedidoId(UUID pedidoId) {
        return pagamentoRepositoryJpa.findByPedidoId(pedidoId).map(pagamentoMapper::toDomain);
    }
    
    @Override
    public List<Pagamento> listarTodos() {
        return pagamentoRepositoryJpa.findAll().stream()
            .map(pagamentoMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void excluir(UUID id) {
        pagamentoRepositoryJpa.deleteById(id);
    }
}
