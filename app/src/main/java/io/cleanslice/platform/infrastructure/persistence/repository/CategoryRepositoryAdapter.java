package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.application.port.out.persistence.CategoryRepository;
import io.cleanslice.platform.domain.Category;
import io.cleanslice.platform.infrastructure.persistence.entity.CategoryEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.CategoryEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryRepositoryAdapter implements CategoryRepository {

    @Inject
    CategoryEntityMapper mapper;

    @Override
    @WithSession
    public Uni<Category> save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        return CategoryEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }

    @Override
    @WithSession
    public Uni<Category> findById(String id) {
        return CategoryEntity.<CategoryEntity>findById(id)
                .onItem().transform(entity -> entity != null ? mapper.toDomain(entity) : null);
    }

    @Override
    @WithSession
    public Uni<List<Category>> findAll() {
        return CategoryEntity.<CategoryEntity>listAll()
                .onItem().transform(entities -> entities.stream()
                        .map(mapper::toDomain)
                        .collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<Void> deleteById(String number) {
        return CategoryEntity.deleteById(number).replaceWithVoid();
    }

    @Override
    @WithSession
    public Uni<Boolean> existsById(String number) {
        return CategoryEntity.findById(number).onItem().transform(entity -> entity != null);
    }
}
