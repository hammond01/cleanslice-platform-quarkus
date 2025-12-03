package application.service;

import application.dto.AuditEvent;
import application.dto.CreateProduct;
import application.dto.GetProduct;
import application.mapper.ProductMapper;
import application.port.outbound.AuditEventPublisherPort;
import application.port.outbound.ProductRepository;
import domain.entity.Product;
import domain.enums.AuditTypeEnum;
import domain.exception.ProductNotFoundException;
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
    ProductRepository productRepository;

    @Inject
    ProductMapper productMapper;

    public List<GetProduct> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public GetProduct getProductById(String id) {
        var product = productRepository.findById(id);

        if (product == null) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return productMapper.toResponse(product);
    }

    @Transactional
    public GetProduct createProduct(CreateProduct request) {
        Product product = productMapper.toEntity(request);

        productRepository.save(product);

        publishCrudEvent("CREATE", product.getNumber(), "Created: " + product.getName());

        return productMapper.toResponse(product);
    }

    @Transactional
    public GetProduct updateProduct(String number, CreateProduct request) {
        var product = productRepository.findById(number);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        productMapper.updateEntity(request, product);

        // Publish audit event
        publishCrudEvent("UPDATE", product.getNumber(), "Updated product: " + product.getName());

        return productMapper.toResponse(product);
    }

    @Transactional
    public void deleteProduct(String number) {
        var product = productRepository.findById(number);

        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        String productName = product.getName();
        productRepository.delete(product);
        product.delete();

        // Publish audit event
        publishCrudEvent("DELETE", number, "Deleted product: " + productName);
    }

    private void publishCrudEvent(String action, String entityId, String details) {
        var event = new AuditEvent();

        event.auditTypeEnum = AuditTypeEnum.CRUD;
        event.action = action;
        event.serviceName = "product-service";
        event.entityType = "Product";
        event.entityId = entityId;
        event.metadata = details;
        event.timestamp = LocalDateTime.now();

        auditEventPublisher.publishCrudEvent(event);
    }
}
