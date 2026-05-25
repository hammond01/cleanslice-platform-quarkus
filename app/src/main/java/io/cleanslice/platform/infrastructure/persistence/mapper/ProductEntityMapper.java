package io.cleanslice.platform.infrastructure.persistence.mapper;

import io.cleanslice.platform.domain.Product;
import io.cleanslice.platform.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductEntityMapper {
    ProductEntity toEntity(Product domain);
    Product toDomain(ProductEntity entity);
}
