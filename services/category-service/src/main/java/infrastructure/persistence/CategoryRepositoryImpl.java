package infrastructure.persistence;

import application.port.outbound.CategoryRepository;
import domain.entity.Category;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CategoryRepositoryImpl implements CategoryRepository {

    @Override
    @WithSession
    public Uni<List<Category>> findAll() {
        return Category.listAll();
    }

    @Override
    @WithSession
    public Uni<Category> findById(String number) {
        return Category.findById(number);
    }

    @Override
    @WithSession
    public Uni<Category> save(Category category) {
        return category.persist().replaceWith(category);
    }

    @Override
    @WithSession
    public Uni<Void> deleteById(String number) {
        return Category.deleteById(number).replaceWithVoid();
    }

    @Override
    public Uni<Boolean> existsById(String number) {
        return Category.findById(number)
                .onItem().transform(c -> c != null);
    }
}
