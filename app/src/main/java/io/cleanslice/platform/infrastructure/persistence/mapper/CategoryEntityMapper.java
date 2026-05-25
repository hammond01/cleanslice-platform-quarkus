package io.cleanslice.platform.infrastructure.persistence.mapper;

import io.cleanslice.platform.domain.Category;
import io.cleanslice.platform.infrastructure.persistence.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryEntityMapper {
    CategoryEntity toEntity(Category domain);
    Category toDomain(CategoryEntity entity);
}
