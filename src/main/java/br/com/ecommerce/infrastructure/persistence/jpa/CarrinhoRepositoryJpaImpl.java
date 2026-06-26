package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.application.repository.CarrinhoRepository;
import br.com.ecommerce.domain.Carrinho;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class CarrinhoRepositoryJpaImpl implements CarrinhoRepository {
    
    private final CarrinhoRepositoryJpa carrinhoRepositoryJpa;
    private final CarrinhoMapper carrinhoMapper;
    
    public CarrinhoRepositoryJpaImpl(@Lazy CarrinhoRepositoryJpa carrinhoRepositoryJpa, CarrinhoMapper carrinhoMapper) {
        this.carrinhoRepositoryJpa = carrinhoRepositoryJpa;
        this.carrinhoMapper = carrinhoMapper;
    }
    
    @Override
    @Transactional
    public void salvar(Carrinho carrinho) {
        CarrinhoEntity entity = carrinhoMapper.toEntity(carrinho);
        carrinhoRepositoryJpa.save(entity);
    }
    
    @Override
    public Optional<Carrinho> buscarPorClienteId(UUID clienteId) {
        return carrinhoRepositoryJpa.findByClienteId(clienteId)
            .map(carrinhoMapper::toDomain);
    }
    
    @Override
    @Transactional
    public void excluirPorClienteId(UUID clienteId) {
        carrinhoRepositoryJpa.deleteByClienteId(clienteId);
    }
    
    @Override
    @Transactional
    public void save(Carrinho carrinho) {
        salvar(carrinho);
    }
}
