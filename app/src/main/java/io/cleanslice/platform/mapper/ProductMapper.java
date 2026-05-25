package io.cleanslice.platform.mapper;

import io.cleanslice.platform.dto.CreateProductRequest;
import io.cleanslice.platform.dto.ProductResponse;
import io.cleanslice.platform.domain.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductMapper {

    @Mapping(source = "RowId", target = "id")
    ProductResponse toResponse(Product product);

    @Mapping(target = "RowId", ignore = true)
    @Mapping(target = "Number", ignore = true)
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
    Product toEntity(CreateProductRequest request);

    @Mapping(target = "RowId", ignore = true)
    @Mapping(target = "Number", ignore = true)
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
    void updateEntity(CreateProductRequest request, @MappingTarget Product product);
}

