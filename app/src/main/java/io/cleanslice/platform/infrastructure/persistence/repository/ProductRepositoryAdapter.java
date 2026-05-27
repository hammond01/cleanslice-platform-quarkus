package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.application.port.out.persistence.ProductRepository;
import io.cleanslice.platform.domain.Product;
import io.cleanslice.platform.infrastructure.persistence.entity.ProductEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.ProductEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductRepositoryAdapter implements ProductRepository {

    @Inject
    ProductEntityMapper mapper;

    @Override
    @WithSession
    public Uni<Product> save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        return ProductEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }

    @Override
    @WithSession
    public Uni<Product> findById(String id) {
        return ProductEntity.<ProductEntity>findById(id)
                .onItem().transform(entity -> entity != null ? mapper.toDomain(entity) : null);
    }

    @Override
    @WithSession
    public Uni<List<Product>> findAll() {
        return ProductEntity.<ProductEntity>listAll()
                .onItem().transform(entities -> entities.stream()
                        .map(mapper::toDomain)
                        .collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<Void> delete(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        return ProductEntity.deleteById(entity.Number).replaceWithVoid();
    }
}
