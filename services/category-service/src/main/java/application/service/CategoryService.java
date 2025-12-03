package application.service;

import domain.entity.Category;
import application.dto.GetCategoryDto;
import application.dto.CreateCategoryDto;
import application.dto.UpdateCategoryDto;
import application.dto.AuditEvent;
import application.port.outbound.AuditEventPublisherPort;
import application.mapper.CategoryMapper;
import domain.enums.AuditType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CategoryService {

    @Inject
    AuditEventPublisherPort auditEventPublisher;
    
    @Inject
    CategoryMapper categoryMapper;

    public List<GetCategoryDto> getAllCategories() {
        return Category.<Category>listAll().stream()
                .filter(c -> !c.isDeleted()) // Filter out soft deleted records
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public GetCategoryDto getCategoryById(Long id) {
        Category category = Category.findById(id);
        if (category == null || category.isDeleted()) {
            throw new RuntimeException("Category not found");
        }
        return categoryMapper.toDto(category);
    }

    @Transactional
    public GetCategoryDto createCategory(CreateCategoryDto dto) {
        Category category = categoryMapper.toEntity(dto);
        if (category.slug == null || category.slug.isEmpty()) {
            category.slug = generateSlug(dto.name);
        }
        category.persist();
        
        // Publish audit event
        publishCrudEvent("CREATE", category.id, "Created category: " + category.name);
        
        return categoryMapper.toDto(category);
    }

    @Transactional
    public GetCategoryDto updateCategory(Long id, UpdateCategoryDto dto) {
        Category category = Category.findById(id);
        if (category == null || category.isDeleted()) {
            throw new RuntimeException("Category not found");
        }
        categoryMapper.updateEntity(dto, category);
        if (category.slug == null || category.slug.isEmpty()) {
            category.slug = generateSlug(dto.name);
        }
        
        // Publish audit event
        publishCrudEvent("UPDATE", category.id, "Updated category: " + category.name);
        
        return categoryMapper.toDto(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = Category.findById(id);
        if (category == null || category.isDeleted()) {
            throw new RuntimeException("Category not found");
        }
        String categoryName = category.name;
        category.softDelete("system"); // Soft delete instead of hard delete
        
        // Publish audit event
        publishCrudEvent("DELETE", id, "Soft deleted category: " + categoryName);
    }
    
    private void publishCrudEvent(String action, Long entityId, String details) {
        AuditEvent event = new AuditEvent();
        event.auditType = AuditType.CRUD;
        event.action = action;
        event.serviceName = "category-service";
        event.entityType = "Category";
        event.entityId = entityId;
        event.metadata = details;
        event.timestamp = LocalDateTime.now();
        
        auditEventPublisher.publishCrudEvent(event);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");
    }
}
