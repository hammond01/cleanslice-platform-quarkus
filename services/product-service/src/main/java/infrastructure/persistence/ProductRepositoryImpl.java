package infrastructure.persistence;

import application.port.outbound.ProductRepository;
import domain.entity.Product;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public void save(Product product) {
        product.persist();
    }

    @Override
    public Product findById(String id) {
        return Product.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return Product.listAll();
    }

    @Override
    public void delete(Product product) {
        product.delete();
    }
}
