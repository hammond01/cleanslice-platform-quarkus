package io.cleanslice.platform.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.cleanslice.platform.domain.Product;
import io.cleanslice.platform.common.exception.ResourceNotFoundException;
import io.cleanslice.platform.dto.CreateProductRequest;
import io.cleanslice.platform.dto.ProductResponse;
import io.cleanslice.platform.application.port.out.persistence.ProductRepository;
import io.cleanslice.platform.domain.enums.AuditTypeEnum;
import io.cleanslice.platform.domain.enums.LogLevel;
import io.cleanslice.platform.common.logging.LoggingHelper;
import io.cleanslice.platform.mapper.ProductMapper;
import io.cleanslice.platform.common.context.UserContext;
import io.cleanslice.platform.dto.AuditEvent;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductService {

    @Inject
    AuditHelper auditHelper;

    @Inject
    ProductRepository productRepository;

    @Inject
    ProductMapper productMapper;

    @Inject
    UserContext userContext;

    @Inject
    LoggingHelper loggingHelper;

    public Uni<List<ProductResponse>> getAllProducts() {
        return productRepository.findAll()
                .onItem().transform(products -> products.stream()
                        .map(productMapper::toResponse)
                        .collect(Collectors.toList()))
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error getting all products: %s", ex.getMessage()));
    }

    public Uni<ProductResponse> getProductById(String id) {
        return productRepository.findById(id)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Product not found with id: " + id))
                .onItem().transform(productMapper::toResponse);
    }

    @WithTransaction
    public Uni<ProductResponse> createProduct(CreateProductRequest request) {
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
        return productRepository.save(product)
                .onItem().invoke(savedProduct -> {
                    Log.infof("Product saved with RowId: %s, Number: %s", savedProduct.RowId, savedProduct.Number);
                    
                    // Manual application log for successful creation
                    loggingHelper.logApp(
                        LogLevel.INFO,
                        String.format("Product created successfully: %s (ID: %s)", savedProduct.name, savedProduct.RowId),
                        userContext.getCurrentUserId(),
                        null
                    );
                    
                    AuditEvent event = auditHelper.createBaseEvent("product-service", AuditTypeEnum.CRUD, "CREATE");
                    event.entityType = "Product";
                    event.rowId = savedProduct.RowId;
                    event.metadata = "Created: " + savedProduct.name;
                    auditHelper.publishCrudEvent(event);
                })
                .onItem().transform(productMapper::toResponse)
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error creating product: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<ProductResponse> updateProduct(String number, CreateProductRequest request) {
        return productRepository.findById(number)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Product not found"))
                .onItem().invoke(product -> productMapper.updateEntity(request, product))
                .onItem().invoke(product -> {
                    AuditEvent event = auditHelper.createBaseEvent("product-service", AuditTypeEnum.CRUD, "UPDATE");
                    event.entityType = "Product";
                    event.rowId = product.RowId;
                    event.metadata = "Updated product: " + product.name;
                    auditHelper.publishCrudEvent(event);
                })
                .onItem().transformToUni(product -> productRepository.save(product))
                .onItem().transform(productMapper::toResponse)
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error updating product: %s", ex.getMessage()));
    }

    @WithTransaction
    public Uni<Void> deleteProduct(String number) {
        return productRepository.findById(number)
                .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Product not found"))
                .onItem().transformToUni(product -> {
                    String productName = product.name;
                    Integer rowId = product.RowId;
                    return productRepository.delete(product)
                            .onItem().invoke(() -> {
                                AuditEvent event = auditHelper.createBaseEvent("product-service", AuditTypeEnum.CRUD, "DELETE");
                                event.entityType = "Product";
                                event.rowId = rowId;
                                event.metadata = "Deleted product: " + productName;
                                auditHelper.publishCrudEvent(event);
                            });
                })
                .onFailure().invoke(ex ->
                        Log.errorf(ex, "Error deleting product: %s", ex.getMessage()));
    }

}


