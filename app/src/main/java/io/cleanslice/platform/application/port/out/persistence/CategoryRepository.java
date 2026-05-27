package io.cleanslice.platform.application.port.out.persistence;

import io.cleanslice.platform.domain.Category;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface CategoryRepository {
    Uni<List<Category>> findAll();
    Uni<Category> findById(String number);
    Uni<Category> save(Category category);
    Uni<Void> deleteById(String number);
    Uni<Boolean> existsById(String number);
}
