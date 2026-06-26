package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.application.repository.CategoriaRepository;
import br.com.ecommerce.domain.Categoria;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class CategoriaRepositoryJpaImpl implements CategoriaRepository {
    
    private final CategoriaRepositoryJpa categoriaRepositoryJpa;
    private final CategoriaMapper categoriaMapper;
    
    public CategoriaRepositoryJpaImpl(@Lazy CategoriaRepositoryJpa categoriaRepositoryJpa, CategoriaMapper categoriaMapper) {
        this.categoriaRepositoryJpa = categoriaRepositoryJpa;
        this.categoriaMapper = categoriaMapper;
    }
    
    @Override
    @Transactional
    public void salvar(Categoria categoria) {
        CategoriaEntity entity = categoriaMapper.toEntity(categoria);
        categoriaRepositoryJpa.save(entity);
    }
    
    @Override
    public Optional<Categoria> buscarPorId(UUID id) {
        return categoriaRepositoryJpa.findById(id).map(categoriaMapper::toDomain);
    }
    
    @Override
    public List<Categoria> listarTodas() {
        return categoriaRepositoryJpa.findAll().stream()
            .map(categoriaMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Categoria> buscarPorCategoriaPai(UUID categoriaPaiId) {
        return categoriaRepositoryJpa.findByCategoriaPaiId(categoriaPaiId).stream()
            .map(categoriaMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Categoria> buscarRaizes() {
        return categoriaRepositoryJpa.findRaizes().stream()
            .map(categoriaMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void excluir(UUID id) {
        categoriaRepositoryJpa.deleteById(id);
    }
}
