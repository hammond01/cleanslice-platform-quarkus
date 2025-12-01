package application.service;

import application.dto.AuditEvent;
import application.dto.ProductRequest;
import application.dto.ProductResponse;
import application.mapper.ProductMapper;
import application.port.outbound.AuditEventPublisherPort;
import domain.entity.Product;
import domain.model.AuditType;
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
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = Product.findById(id);
        if (product == null) {
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
        if (product == null) {
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
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        String productName = product.name;
        product.delete();
        
        // Publish audit event
        publishCrudEvent("DELETE", id, "Deleted product: " + productName);
    }
    
    private void publishCrudEvent(String action, Long entityId, String details) {
        AuditEvent event = new AuditEvent();
        event.auditType = AuditType.CRUD;
        event.action = action;
        event.serviceName = "product-service";
        event.entityType = "Product";
        event.entityId = entityId;
        event.metadata = details;
        event.timestamp = LocalDateTime.now();
        
        auditEventPublisher.publishCrudEvent(event);
    }
}
