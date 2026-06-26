package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.Marca;
import org.springframework.stereotype.Component;

@Component
public class MarcaMapper {
    
    public Marca toDomain(MarcaEntity entity) {
        if (entity == null) return null;
        
        // Usa o construtor de reconstituição (com todos os campos)
        return new Marca(
            entity.getId(),
            entity.getNome(),
            entity.getDescricao(),
            entity.getSiteOficial(),
            entity.getEmailContato(),
            entity.getTelephoneContato(),
            entity.getLogoUrl(),
            entity.isAtiva(),
            entity.getDataCadastro(),
            entity.getDataAtualizacao()
        );
    }
    
    public MarcaEntity toEntity(Marca domain) {
        if (domain == null) return null;
        
        MarcaEntity entity = new MarcaEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setDescricao(domain.getDescricao());
        entity.setSiteOficial(domain.getSiteOficial());
        entity.setEmailContato(domain.getEmailContato());
        entity.setTelephoneContato(domain.getTelefoneContato());
        entity.setLogoUrl(domain.getLogoUrl());
        entity.setAtiva(domain.isAtiva());
        entity.setDataCadastro(domain.getDataCadastro());
        entity.setDataAtualizacao(domain.getDataAtualizacao());
        
        return entity;
    }
}
