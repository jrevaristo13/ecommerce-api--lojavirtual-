package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.application.repository.MarcaRepository;
import br.com.ecommerce.domain.Marca;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class MarcaRepositoryJpaImpl implements MarcaRepository {
    
    private final MarcaRepositoryJpa marcaRepositoryJpa;
    private final MarcaMapper marcaMapper;
    
    public MarcaRepositoryJpaImpl(@Lazy MarcaRepositoryJpa marcaRepositoryJpa, MarcaMapper marcaMapper) {
        this.marcaRepositoryJpa = marcaRepositoryJpa;
        this.marcaMapper = marcaMapper;
    }
    
    @Override
    @Transactional
    public void salvar(Marca marca) {
        MarcaEntity entity = marcaMapper.toEntity(marca);
        marcaRepositoryJpa.save(entity);
    }
    
    @Override
    public Optional<Marca> buscarPorId(UUID id) {
        return marcaRepositoryJpa.findById(id).map(marcaMapper::toDomain);
    }
    
    @Override
    public List<Marca> listarTodas() {
        return marcaRepositoryJpa.findAll().stream()
            .map(marcaMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Optional<Marca> buscarPorNome(String nome) {
        return marcaRepositoryJpa.findByNome(nome).map(marcaMapper::toDomain);
    }
    
    @Override
    @Transactional
    public void excluir(UUID id) {
        marcaRepositoryJpa.deleteById(id);
    }
}
