package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.application.repository.ProdutoRepository;
import br.com.ecommerce.domain.Produto;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ProdutoRepositoryJpaImpl implements ProdutoRepository {

    private final ProdutoRepositoryJpa produtoRepositoryJpa;
    private final ProdutoMapper produtoMapper;

    public ProdutoRepositoryJpaImpl(@Lazy ProdutoRepositoryJpa produtoRepositoryJpa, ProdutoMapper produtoMapper) {
        this.produtoRepositoryJpa = produtoRepositoryJpa;
        this.produtoMapper = produtoMapper;
    }

    @Override
    public void salvar(Produto produto) {
        ProdutoEntity entity = produtoMapper.toEntity(produto);
        produtoRepositoryJpa.save(entity);
    }

    @Override
    public Optional<Produto> buscarPorId(UUID id) {
        return produtoRepositoryJpa.findById(id).map(produtoMapper::toDomain);
    }

    @Override
    public List<Produto> listarTodos() {
        return produtoRepositoryJpa.findAll().stream()
                .map(produtoMapper::toDomain)
                .collect(Collectors.toList());
    }
}
