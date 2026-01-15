package application.service;

import application.dto.*;
import application.mapper.ProductMapper;
import application.port.outbound.*;
import domain.entity.Product;
import share.dto.AuditEvent;
import share.enums.AuditTypeEnum;
import share.enums.LogLevel;
import domain.exception.ProductNotFoundException;
import infrastructure.persistence.UserContext;
import infrastructure.logging.DatabaseOperationLogger;
import infrastructure.logging.LoggingHelper;
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
public class ProductService {

    @Inject
    AuditEventPublisherPort auditEventPublisher;

    @Inject
    ProductRepository productRepository;

    @Inject
    ProductMapper productMapper;

    @Inject
    UserContext userContext;

    @Inject
    LoggingHelper loggingHelper;

    public Uni<List<GetProduct>> getAllProducts() {
        return productRepository.findAll()
                .onItem().transform(products -> products.stream()
                        .map(productMapper::toResponse)
                        .collect(Collectors.toList()))
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error getting all products: %s", ex.getMessage()));
    }

    public Uni<GetProduct> getProductById(String id) {
        return productRepository.findById(id)
                .onItem().ifNull().failWith(() -> new ProductNotFoundException("Product not found with id: " + id))
                .onItem().transform(productMapper::toResponse);
    }

    @WithTransaction
    public Uni<GetProduct> createProduct(CreateProduct request) {
        Product product = productMapper.toEntity(request);
        
        // Todo: Generate unique Number for the product
        product.Number = "PRD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Manual application log for business event
        loggingHelper.logApp(
            LogLevel.INFO,
            String.format("Starting creation of product: %s", request.name),
            userContext.getCurrentUserId(),
            null
        );

        // Automatic DB operation logging with timing
        return DatabaseOperationLogger.logPersist(product, 
                productRepository.save(product)
                    .call(savedProduct -> savedProduct.flush())
                )
                .onItem().invoke(savedProduct -> {
                    try {
                        Log.infof("Product saved with RowId: %s, Number: %s", savedProduct.RowId, savedProduct.Number);
                        
                        // Manual application log for successful creation
                        loggingHelper.logApp(
                            LogLevel.INFO,
                            String.format("Product created successfully: %s (ID: %s)", savedProduct.name, savedProduct.RowId),
                            userContext.getCurrentUserId(),
                            null
                        );
                        
                        publishCrudEvent("CREATE", savedProduct.RowId, "Created: " + savedProduct.name);
                    } catch (Exception ex) {
                        Log.warnf(ex, "Failed to publish audit event for product creation: %s", ex.getMessage());
                    }
                })
                .onItem().transform(productMapper::toResponse)
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error creating product: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<GetProduct> updateProduct(String number, CreateProduct request) {
        return productRepository.findById(number)
                .onItem().ifNull().failWith(() -> new ProductNotFoundException("Product not found"))
                .onItem().invoke(product -> productMapper.updateEntity(request, product))
                .onItem().invoke(product -> {
                    try {
                        publishCrudEvent("UPDATE", product.RowId, "Updated product: " + product.name);
                    } catch (Exception ex) {
                        Log.warnf(ex, "Failed to publish audit event for product update: %s", ex.getMessage());
                    }
                })
                .onItem().transform(productMapper::toResponse)
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error updating product: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<Void> deleteProduct(String number) {
        return productRepository.findById(number)
                .onItem().ifNull().failWith(() -> new ProductNotFoundException("Product not found"))
                .onItem().transformToUni(product -> {
                    String productName = product.name;
                    Integer rowId = product.RowId;
                    return productRepository.delete(product)
                            .onItem().invoke(() -> {
                                try {
                                    publishCrudEvent("DELETE", rowId, "Deleted product: " + productName);
                                } catch (Exception ex) {
                                    Log.warnf(ex, "Failed to publish audit event for product deletion: %s", ex.getMessage());
                                }
                            });
                })
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error deleting product: %s", ex.getMessage()));
    }

    private void publishCrudEvent(String action, Integer rowId, String details) {
        publishCrudEvent(action, rowId, details, null);
    }

    private void publishCrudEvent(String action, Integer rowId, String details, String oldValue) {
        try {
            var event = new AuditEvent();
            String correlationId = UUID.randomUUID().toString();

            event.auditTypeEnum = AuditTypeEnum.CRUD;
            event.action = action;
            event.serviceName = "product-service";
            event.entityType = "Product";
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
            Log.infof("Published audit event [%s] for %s: %s", correlationId, action, rowId);
        } catch (Exception ex) {
            Log.errorf(ex, "Critical: Failed to publish audit event for %s on %s", action, rowId);
        }
    }
}
