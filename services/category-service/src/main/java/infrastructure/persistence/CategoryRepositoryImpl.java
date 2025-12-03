package infrastructure.persistence;

import application.port.outbound.CategoryRepository;
import domain.entity.Category;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class CategoryRepositoryImpl implements CategoryRepository {

    @Override
    public Uni<List<Category>> findAll() {
        return Category.listAll();
    }

    @Override
    public Uni<Category> findById(Long id) {
        return Category.findById(id);
    }

    @Override
    public Uni<Category> save(Category category) {
        return category.persist().replaceWith(category);
    }

    @Override
    public Uni<Void> deleteById(Long id) {
        return Category.deleteById(id).replaceWithVoid();
    }

    @Override
    public Uni<Boolean> existsById(Long id) {
        return Category.findById(id)
                .onItem().transform(c -> c != null);
    }
}
