package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    
    public Categoria toDomain(CategoriaEntity entity) {
        if (entity == null) return null;
        
        return new Categoria(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getCategoriaPaiId(),
            entity.isAtiva(),
            entity.getDataCadastro(),
            entity.getUltimaAtualizacao()
        );
    }
    
    public CategoriaEntity toEntity(Categoria domain) {
        if (domain == null) return null;
        
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setCategoriaPaiId(domain.getCategoriaPaiId());
        entity.setAtiva(domain.isAtiva());
        entity.setDataCadastro(domain.getDataCadastro());
        entity.setUltimaAtualizacao(domain.getUltimaAtualizacao());
        
        return entity;
    }
}
