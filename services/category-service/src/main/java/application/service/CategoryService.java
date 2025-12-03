package application.service;

import domain.entity.Category;
import domain.exception.CategoryNotFoundException;
import application.dto.GetCategoryDto;
import application.dto.CreateCategoryDto;
import application.dto.UpdateCategoryDto;
import application.dto.AuditEvent;
import application.port.outbound.AuditEventPublisherPort;
import application.port.outbound.CategoryRepository;
import application.mapper.CategoryMapper;
import domain.enums.AuditType;
import infrastructure.persistence.UserContext;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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

    public Uni<GetCategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .onItem().ifNull().failWith(() -> new CategoryNotFoundException(id))
                .onItem().invoke(category -> {
                    if (category.isDeleted()) {
                        throw new CategoryNotFoundException(id);
                    }
                })
                .onItem().transform(categoryMapper::toDto);
    }

    @WithTransaction
    public Uni<GetCategoryDto> createCategory(CreateCategoryDto dto) {
        Category category = categoryMapper.toEntity(dto);
        if (category.slug == null || category.slug.isEmpty()) {
            category.slug = generateSlug(dto.name);
        }
        return categoryRepository.save(category)
                .onItem().invoke(savedCategory -> {
                    try {
                        publishCrudEvent("CREATE", savedCategory.id, "Created category: " + savedCategory.name);
                    } catch (Exception ex) {
                        Log.warnf(ex, "Failed to publish audit event for category creation: %s", ex.getMessage());
                    }
                })
                .onItem().transform(categoryMapper::toDto)
                .onFailure().invoke(ex -> 
                        Log.errorf(ex, "Error creating category: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<GetCategoryDto> updateCategory(Long id, UpdateCategoryDto dto) {
        return categoryRepository.findById(id)
                .onItem().ifNull().failWith(() -> new CategoryNotFoundException(id))
                .onItem().invoke(category -> {
                    if (category.isDeleted()) {
                        throw new CategoryNotFoundException(id);
                    }
                })
                .onItem().invoke(category -> {
                    categoryMapper.updateEntity(dto, category);
                    if (category.slug == null || category.slug.isEmpty()) {
                        category.slug = generateSlug(dto.name);
                    }
                })
                .onItem().invoke(category -> {
                    try {
                        publishCrudEvent("UPDATE", category.id, "Updated category: " + category.name);
                    } catch (Exception ex) {
                        Log.warnf(ex, "Failed to publish audit event for category update: %s", ex.getMessage());
                    }
                })
                .onItem().transform(categoryMapper::toDto)
                .onFailure().invoke(ex -> 
                        Log.errorf(ex, "Error updating category: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<Void> deleteCategory(Long id) {
        return categoryRepository.findById(id)
                .onItem().ifNull().failWith(() -> new CategoryNotFoundException(id))
                .onItem().invoke(category -> {
                    if (category.isDeleted()) {
                        throw new CategoryNotFoundException(id);
                    }
                })
                .onItem().transformToUni(category -> {
                    String categoryName = category.name;
                    category.softDelete("system");
                    try {
                        publishCrudEvent("DELETE", id, "Soft deleted category: " + categoryName);
                    } catch (Exception ex) {
                        Log.warnf(ex, "Failed to publish audit event for category deletion: %s", ex.getMessage());
                    }
                    return Uni.createFrom().voidItem();
                })
                .onFailure().invoke(ex -> 
                        Log.errorf(ex, "Error deleting category: %s", ex.getMessage()));
    }
    
    private void publishCrudEvent(String action, Long entityId, String details) {
        publishCrudEvent(action, entityId, details, null);
    }

    private void publishCrudEvent(String action, Long entityId, String details, String oldValue) {
        try {
            AuditEvent event = new AuditEvent();
            String correlationId = UUID.randomUUID().toString();
            
            event.auditType = AuditType.CRUD;
            event.action = action;
            event.serviceName = "category-service";
            event.entityType = "Category";
            event.entityId = entityId;
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
            Log.debugf("Published audit event [%s] for %s: %s", correlationId, action, entityId);
        } catch (Exception ex) {
            Log.errorf(ex, "Critical: Failed to publish audit event for %s on %s", action, entityId);
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
