package com.honeybee.product.application.service;

import com.honeybee.product.domain.entity.Product;
import com.honeybee.product.application.dto.ProductRequest;
import com.honeybee.product.application.dto.ProductResponse;
import com.honeybee.product.application.dto.AuditEvent;
import com.honeybee.product.application.port.outbound.AuditEventPublisherPort;
import com.honeybee.product.application.mapper.ProductMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductService {

    @Inject
    AuditEventPublisherPort auditEventPublisher;
    
    @Inject
    ProductMapper productMapper;

    public List<ProductResponse> getAllProducts() {
        return Product.<Product>listAll().stream()
                .filter(p -> !p.isDeleted()) // Filter out soft deleted records
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = Product.findById(id);
        if (product == null || product.isDeleted()) {
            throw new RuntimeException("Product not found");
        }
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        product.persist();
        
        // Publish audit event
        publishCrudEvent("CREATE", product.id, "Created product: " + product.name);
        
        return productMapper.toResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = Product.findById(id);
        if (product == null || product.isDeleted()) {
            throw new RuntimeException("Product not found");
        }
        productMapper.updateEntity(request, product);
        
        // Publish audit event
        publishCrudEvent("UPDATE", product.id, "Updated product: " + product.name);
        
        return productMapper.toResponse(product);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        Product product = Product.findById(id);
        if (product == null || product.isDeleted()) {
            throw new RuntimeException("Product not found");
        }
        String productName = product.name;
        product.softDelete("system"); // Soft delete instead of hard delete
        
        // Publish audit event
        publishCrudEvent("DELETE", id, "Soft deleted product: " + productName);
    }
    
    private void publishCrudEvent(String action, Long entityId, String details) {
        AuditEvent event = new AuditEvent();
        event.auditType = com.honeybee.product.domain.model.AuditType.CRUD;
        event.action = action;
        event.serviceName = "product-service";
        event.entityType = "Product";
        event.entityId = entityId;
        event.metadata = details;
        event.timestamp = LocalDateTime.now();
        
        auditEventPublisher.publishCrudEvent(event);
    }
}
