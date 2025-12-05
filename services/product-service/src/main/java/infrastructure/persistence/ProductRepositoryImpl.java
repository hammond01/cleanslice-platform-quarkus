package infrastructure.persistence;

import application.port.outbound.ProductRepository;
import domain.entity.Product;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepositoryImpl implements ProductRepository {

    @Override
    @WithSession
    public Uni<Product> save(Product product) {
        return product.persist().replaceWith(product);
    }

    @Override
    @WithSession
    public Uni<Product> findById(String id) {
        return Product.findById(id);
    }

    @Override
    @WithSession
    public Uni<List<Product>> findAll() {
        return Product.listAll();
    }

    @Override
    @WithSession
    public Uni<Void> delete(Product product) {
        return product.delete().replaceWithVoid();
    }
}
