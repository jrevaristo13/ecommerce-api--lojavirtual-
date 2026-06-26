package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    
    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;
        
        return new Usuario(
            entity.getId(),
            entity.getEmail(),
            entity.getLogin(),
            entity.getSenha(),
            entity.isStatusAtivo(),
            entity.getDataCadastro(),
            entity.getUltimaAtualizacao()
        );
    }
    
    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;
        
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setLogin(domain.getLogin());
        entity.setSenha(domain.getSenha());
        entity.setEmail(domain.getEmail());
        entity.setStatusAtivo(domain.isStatusAtivo());
        entity.setDataCadastro(domain.getDataCadastro());
        entity.setUltimaAtualizacao(domain.getUltimaAtualizacao());
        
        return entity;
    }
}
