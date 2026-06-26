package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.application.repository.UsuarioRepository;
import br.com.ecommerce.domain.Usuario;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRepositoryJpaImpl implements UsuarioRepository {
    
    private final UsuarioRepositoryJpa usuarioRepositoryJpa;
    private final UsuarioMapper usuarioMapper;
    
    public UsuarioRepositoryJpaImpl(@Lazy UsuarioRepositoryJpa usuarioRepositoryJpa, UsuarioMapper usuarioMapper) {
        this.usuarioRepositoryJpa = usuarioRepositoryJpa;
        this.usuarioMapper = usuarioMapper;
    }
    
    @Override
    @Transactional
    public void salvar(Usuario usuario) {
        UsuarioEntity entity = usuarioMapper.toEntity(usuario);
        usuarioRepositoryJpa.save(entity);
    }
    
    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return usuarioRepositoryJpa.findById(id).map(usuarioMapper::toDomain);
    }
    
    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepositoryJpa.findByEmail(email).map(usuarioMapper::toDomain);
    }
    
    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepositoryJpa.findByLogin(login).map(usuarioMapper::toDomain);
    }
    
    @Override
    @Transactional
    public void excluir(UUID id) {
        usuarioRepositoryJpa.deleteById(id);
    }
}
