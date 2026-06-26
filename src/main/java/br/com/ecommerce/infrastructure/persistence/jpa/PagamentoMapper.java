package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.Pagamento;
import org.springframework.stereotype.Component;

@Component
public class PagamentoMapper {
    
    public Pagamento toDomain(PagamentoEntity entity) {
        if (entity == null) return null;
        
        return new Pagamento(
            entity.getId(),
            entity.getPedidoId(),
            entity.getValor(),
            entity.getFormaPagamento(),
            entity.getStatus(),
            entity.getTransacaoGatewayId(),
            entity.getDataCriacao()
        );
    }
    
    public PagamentoEntity toEntity(Pagamento domain) {
        if (domain == null) return null;
        
        PagamentoEntity entity = new PagamentoEntity();
        entity.setId(domain.getId());
        entity.setPedidoId(domain.getPedidoId());
        entity.setValor(domain.getValor());
        entity.setFormaPagamento(domain.getFormaPagamento());
        entity.setStatus(domain.getStatus());
        entity.setTransacaoGatewayId(domain.getTransacaoGatewayId());
        entity.setDataCriacao(domain.getDataCriacao());
        
        return entity;
    }
}
