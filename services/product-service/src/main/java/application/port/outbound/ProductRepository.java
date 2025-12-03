package application.port.outbound;

import domain.entity.Product;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface ProductRepository {
    Uni<Product> save(Product product);

    Uni<Product> findById(String id);

    Uni<List<Product>> findAll();

    Uni<Void> delete(Product product);
}