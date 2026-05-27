package io.cleanslice.platform.service;

import io.cleanslice.platform.domain.Category;
import io.cleanslice.platform.common.exception.ResourceNotFoundException;
import io.cleanslice.platform.dto.GetCategoryDto;
import io.cleanslice.platform.dto.CreateCategoryDto;
import io.cleanslice.platform.dto.UpdateCategoryDto;
import io.cleanslice.platform.application.port.out.persistence.CategoryRepository;
import io.cleanslice.platform.domain.enums.AuditTypeEnum;
import io.cleanslice.platform.mapper.CategoryMapper;
import io.cleanslice.platform.dto.AuditEvent;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryService {

    @Inject
    AuditHelper auditHelper;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CategoryMapper categoryMapper;


    public Uni<List<GetCategoryDto>> getAllCategories() {
        return categoryRepository.findAll()
                .onItem().transform(categories -> categories.stream()
                        .filter(c -> !c.isDeleted())
                        .map(categoryMapper::toDto)
                        .collect(Collectors.toList()))
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error getting all categories: %s", ex.getMessage()));
    }

    public Uni<GetCategoryDto> getCategoryById(String number) {
        return categoryRepository.findById(number)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Category not found with number: " + number))
                .onItem().invoke(Unchecked.consumer(category -> {
                    if (category.isDeleted()) {
                        throw new ResourceNotFoundException("Category not found with number: " + number);
                    }
                }))
                .onItem().transform(categoryMapper::toDto);
    }

    @WithTransaction
    public Uni<GetCategoryDto> createCategory(CreateCategoryDto dto) {
        Category category = categoryMapper.toEntity(dto);
        
        // Todo: Generate unique Number for the category
        category.Number = "CAT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        if (category.slug == null || category.slug.isEmpty()) {
            category.slug = generateSlug(dto.name);
        }
        return categoryRepository.save(category)
                .onItem().invoke(savedCategory -> {
                    Log.infof("Category saved with RowId: %s, Number: %s", savedCategory.RowId, savedCategory.Number);
                    AuditEvent event = auditHelper.createBaseEvent("category-service", AuditTypeEnum.CRUD, "CREATE");
                    event.entityType = "Category";
                    event.rowId = savedCategory.RowId;
                    event.metadata = "Created category: " + savedCategory.name;
                    auditHelper.publishCrudEvent(event);
                })
                .onItem().transform(categoryMapper::toDto)
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error creating category: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<GetCategoryDto> updateCategory(String number, UpdateCategoryDto dto) {
        return categoryRepository.findById(number)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Category not found with number: " + number))
                .onItem().invoke(Unchecked.consumer(category -> {
                    if (category.isDeleted()) {
                        throw new ResourceNotFoundException("Category not found with number: " + number);
                    }
                }))
                .onItem().invoke(category -> {
                    categoryMapper.updateEntity(dto, category);
                    if (category.slug == null || category.slug.isEmpty()) {
                        category.slug = generateSlug(dto.name);
                    }
                })
                .onItem().invoke(category -> {
                    AuditEvent event = auditHelper.createBaseEvent("category-service", AuditTypeEnum.CRUD, "UPDATE");
                    event.entityType = "Category";
                    event.rowId = category.RowId;
                    event.metadata = "Updated category: " + category.name;
                    auditHelper.publishCrudEvent(event);
                })
                .onItem().transformToUni(category -> categoryRepository.save(category))
                .onItem().transform(categoryMapper::toDto)
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error updating category: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<Void> deleteCategory(String number) {
        return categoryRepository.findById(number)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Category not found with number: " + number))
                .onItem().invoke(Unchecked.consumer(category -> {
                    if (category.isDeleted()) {
                        throw new ResourceNotFoundException("Category not found with number: " + number);
                    }
                }))
                .onItem().transformToUni(category -> {
                    String categoryName = category.name;
                    category.softDelete("system");
                    AuditEvent event = auditHelper.createBaseEvent("category-service", AuditTypeEnum.CRUD, "DELETE");
                    event.entityType = "Category";
                    event.rowId = category.RowId;
                    event.metadata = "Soft deleted category: " + categoryName;
                    auditHelper.publishCrudEvent(event);
                    return categoryRepository.save(category).replaceWithVoid();
                })
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error deleting category: %s", ex.getMessage()));
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}


