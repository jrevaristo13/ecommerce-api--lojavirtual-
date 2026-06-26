package br.com.ecommerce.application.usecase;

import br.com.ecommerce.domain.Produto;
import br.com.ecommerce.application.repository.ProdutoRepository;
import org.springframework.stereotype.Service; // ADICIONE ESTE IMPORT
import java.util.List;
import java.util.stream.Collectors;

@Service // ADICIONE ESTA ANOTAÇÃO
public class ListarProdutosUseCase {

    private final ProdutoRepository produtoRepository;

    public ListarProdutosUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> executar(boolean apenasAtivos) {
        List<Produto> produtos = produtoRepository.listarTodos();
        
        if (apenasAtivos) {
            return produtos.stream()
                           .filter(p -> p.isAtivo()) // Garanta que seu Produto tenha esse método
                           .collect(Collectors.toList());
        }
        
        return produtos;
    }
}
