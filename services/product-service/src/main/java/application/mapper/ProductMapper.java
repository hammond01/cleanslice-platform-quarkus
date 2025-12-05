package application.mapper;

import application.dto.CreateProduct;
import application.dto.GetProduct;
import domain.entity.Product;
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
    GetProduct toResponse(Product product);

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
    Product toEntity(CreateProduct request);

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
    void updateEntity(CreateProduct request, @MappingTarget Product product);
}
