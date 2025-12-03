package application.port.outbound;

import domain.entity.Product;

import java.util.List;

public interface ProductRepository {
    void save(Product product);

    Product findById(String id);

    List<Product> findAll();

    void delete(Product product);
}