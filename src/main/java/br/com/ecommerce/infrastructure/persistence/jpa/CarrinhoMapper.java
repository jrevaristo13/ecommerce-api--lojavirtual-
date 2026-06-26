package br.com.ecommerce.infrastructure.persistence.jpa;

import br.com.ecommerce.domain.Carrinho;
import br.com.ecommerce.domain.Cupom;
import br.com.ecommerce.domain.ItemCarrinho;
import br.com.ecommerce.domain.TipoDescontos.TipoDesconto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CarrinhoMapper {
    
    public CarrinhoEntity toEntity(Carrinho domain) {
        if (domain == null) return null;
        
        CarrinhoEntity entity = new CarrinhoEntity();
        entity.setId(domain.getId());
        entity.setClienteId(domain.getClienteId());
        entity.setStatus(CarrinhoEntity.StatusCarrinho.valueOf(domain.getStatus().name()));
        entity.setDataCriacao(domain.getDataCriacao());
        entity.setDataAtualizacao(domain.getDataAtualizacao());
        
        // Mapear itens
        if (domain.getItens() != null) {
            entity.setItens(domain.getItens().stream()
                .map(item -> toItemEntity(item, entity))
                .collect(Collectors.toList()));
        }
        
        // Mapear cupom
        if (domain.getCupomAplicado() != null) {
            entity.setCupomAplicado(toCupomEntity(domain.getCupomAplicado()));
        }
        
        return entity;
    }
    
    public Carrinho toDomain(CarrinhoEntity entity) {
        if (entity == null) return null;
        
        Carrinho.StatusCarrinho status = Carrinho.StatusCarrinho.valueOf(entity.getStatus().name());
        
        java.util.List<ItemCarrinho> itens = entity.getItens().stream()
            .map(this::toItemDomain)
            .collect(Collectors.toList());
        
        Cupom cupom = entity.getCupomAplicado() != null ? toCupomDomain(entity.getCupomAplicado()) : null;
        
        return new Carrinho(
            entity.getId(),
            entity.getClienteId(),
            itens,
            cupom,
            status,
            entity.getDataCriacao(),
            entity.getDataAtualizacao()
        );
    }
    
    private ItemCarrinhoEntity toItemEntity(ItemCarrinho domain, CarrinhoEntity carrinhoEntity) {
        ItemCarrinhoEntity entity = new ItemCarrinhoEntity();
        entity.setCarrinho(carrinhoEntity);
        entity.setProdutoId(domain.getProdutoId());
        entity.setNomeProduto(domain.getNomeProduto());
        entity.setQuantidade(domain.getQuantidade());
        entity.setPrecoUnitario(domain.getPrecoUnitario());
        return entity;
    }
    
    private ItemCarrinho toItemDomain(ItemCarrinhoEntity entity) {
        return new ItemCarrinho(
            entity.getProdutoId(),
            entity.getNomeProduto(),
            entity.getQuantidade(),
            entity.getPrecoUnitario()
        );
    }
    
    private CupomEntity toCupomEntity(Cupom domain) {
        CupomEntity entity = new CupomEntity();
        entity.setId(domain.getId());
        entity.setCodigo(domain.getCodigo());
        entity.setDescricao(domain.getDescricao());
        entity.setTipoDesconto(CupomEntity.TipoDesconto.valueOf(domain.getTipoDesconto().name()));
        entity.setValorDesconto(domain.getValorDesconto());
        entity.setValorMinimoPedido(domain.getValorMinimoPedido());
        entity.setLimiteUso(domain.getLimiteUso());
        entity.setQuantidadeUtilizada(domain.getQuantidadeUtilizada());
        entity.setAtivo(domain.isAtivo());
        entity.setDataCadastro(domain.getDataCadastro());
        entity.setDataAtualizacao(domain.getDataAtualizacao());
        entity.setDataInicio(domain.getDataInicio());
        entity.setDataFim(domain.getDataFim());
        return entity;
    }
    
    private Cupom toCupomDomain(CupomEntity entity) {
        return new Cupom(
            entity.getId(),
            entity.getCodigo(),
            entity.getDescricao(),
            TipoDesconto.valueOf(entity.getTipoDesconto().name()),
            entity.getValorDesconto(),
            entity.getValorMinimoPedido(),
            entity.getLimiteUso(),
            entity.getQuantidadeUtilizada(),
            entity.getAtivo(),
            entity.getDataCadastro(),
            entity.getDataAtualizacao(),
            entity.getDataInicio(),
            entity.getDataFim()
        );
    }
}
