package br.com.ecommerce.infrastructure.web.config;

import br.com.ecommerce.domain.Sku;
import br.com.ecommerce.application.repository.ProdutoRepository; // Importe o repositório correto
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {
    private final ProdutoRepository repository; // Agora usamos o nome correto

    public DataInitializer(ProdutoRepository repository) { 
        this.repository = repository; 
    }

    @Override
    public void run(String... args) throws Exception {
        // Como o seu ProdutoRepositoryInMemory já tem um construtor 
        // que cria um SKU fixo, talvez você nem precise deste IF aqui.
        // O seu repositório já se autopoloupa ao iniciar!
    }
}
