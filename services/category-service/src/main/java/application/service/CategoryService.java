package application.service;

import domain.entity.Category;
import domain.exception.CategoryNotFoundException;
import application.dto.GetCategoryDto;
import application.dto.CreateCategoryDto;
import application.dto.UpdateCategoryDto;
import share.dto.AuditEvent;
import application.port.outbound.AuditEventPublisherPort;
import application.port.outbound.CategoryRepository;
import application.mapper.CategoryMapper;
import infrastructure.persistence.UserContext;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import share.enums.AuditTypeEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryService {

    @Inject
    AuditEventPublisherPort auditEventPublisher;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    CategoryMapper categoryMapper;

    @Inject
    UserContext userContext;

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
                .onItem().ifNull().failWith(() -> new CategoryNotFoundException(number, true))
                .onItem().invoke(Unchecked.consumer(category -> {
                    if (category.isDeleted()) {
                        throw new CategoryNotFoundException(number, true);
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
                .call(savedCategory -> savedCategory.flush()) 
                .onItem().invoke(savedCategory -> {
                    try {
                        Log.infof("Category saved with RowId: %s, Number: %s", savedCategory.RowId, savedCategory.Number);
                        publishCrudEvent("CREATE", savedCategory.RowId, "Created category: " + savedCategory.name);
                    } catch (Exception ex) {
                        Log.warnf(ex, "Failed to publish audit event for category creation: %s", ex.getMessage());
                    }
                })
                .onItem().transform(categoryMapper::toDto)
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error creating category: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<GetCategoryDto> updateCategory(String number, UpdateCategoryDto dto) {
        return categoryRepository.findById(number)
                .onItem().ifNull().failWith(() -> new CategoryNotFoundException(number, true))
                .onItem().invoke(Unchecked.consumer(category -> {
                    if (category.isDeleted()) {
                        throw new CategoryNotFoundException(number, true);
                    }
                }))
                .onItem().invoke(category -> {
                    categoryMapper.updateEntity(dto, category);
                    if (category.slug == null || category.slug.isEmpty()) {
                        category.slug = generateSlug(dto.name);
                    }
                })
                .onItem().invoke(category -> {
                    try {
                        publishCrudEvent("UPDATE", category.RowId, "Updated category: " + category.name);
                    } catch (Exception ex) {
                        Log.warnf(ex, "Failed to publish audit event for category update: %s", ex.getMessage());
                    }
                })
                .onItem().transform(categoryMapper::toDto)
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error updating category: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<Void> deleteCategory(String number) {
        return categoryRepository.findById(number)
                .onItem().ifNull().failWith(() -> new CategoryNotFoundException(number, true))
                .onItem().invoke(Unchecked.consumer(category -> {
                    if (category.isDeleted()) {
                        throw new CategoryNotFoundException(number, true);
                    }
                }))
                .onItem().transformToUni(category -> {
                    String categoryName = category.name;
                    category.softDelete("system");
                    try {
                        publishCrudEvent("DELETE", category.RowId, "Soft deleted category: " + categoryName);
                    } catch (Exception ex) {
                        Log.warnf(ex, "Failed to publish audit event for category deletion: %s", ex.getMessage());
                    }
                    return Uni.createFrom().voidItem();
                })
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error deleting category: %s", ex.getMessage()));
    }

    private void publishCrudEvent(String action, Integer number, String details) {
        publishCrudEvent(action, number, details, null);
    }

    private void publishCrudEvent(String action, Integer rowId, String details, String oldValue) {
        try {
            AuditEvent event = new AuditEvent();
            String correlationId = UUID.randomUUID().toString();

            event.auditTypeEnum = AuditTypeEnum.CRUD;
            event.action = action;
            event.serviceName = "category-service";
            event.entityType = "Category";
            event.rowId = rowId;
            event.metadata = details;
            event.timestamp = LocalDateTime.now();
            event.correlationId = correlationId;

            // Add user context
            try {
                event.username = userContext.getUsername();
                event.ipAddress = userContext.getIpAddress();
                if (userContext.getCurrentUserId() != null && !"system".equals(userContext.getCurrentUserId())) {
                    event.userId = Long.parseLong(userContext.getCurrentUserId());
                }
            } catch (Exception ex) {
                Log.warnf("Failed to extract user context for audit: %s", ex.getMessage());
            }

            // Add old/new values for UPDATE actions
            if ("UPDATE".equals(action) && oldValue != null) {
                event.oldValue = oldValue;
                event.newValue = details;
            }

            auditEventPublisher.publishCrudEvent(event);
            Log.debugf("Published audit event [%s] for %s: %s", correlationId, action, rowId);
        } catch (Exception ex) {
            Log.errorf(ex, "Critical: Failed to publish audit event for %s on %s", action, rowId);
        }
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
