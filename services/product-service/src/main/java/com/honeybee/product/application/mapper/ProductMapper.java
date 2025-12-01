package com.honeybee.product.application.mapper;

import com.honeybee.product.application.dto.ProductRequest;
import com.honeybee.product.application.dto.ProductResponse;
import com.honeybee.product.domain.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {
    
    ProductResponse toResponse(Product product);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "rowVersion", ignore = true)
    @Mapping(target = "modificationStatus", ignore = true)
    @Mapping(target = "active", constant = "true")
    Product toEntity(ProductRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "lockedAt", ignore = true)
    @Mapping(target = "lockedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "rowVersion", ignore = true)
    @Mapping(target = "modificationStatus", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}
