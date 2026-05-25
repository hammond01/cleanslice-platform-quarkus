package io.cleanslice.platform.infrastructure.persistence.mapper;

import io.cleanslice.platform.domain.ErrorLog;
import io.cleanslice.platform.infrastructure.persistence.entity.ErrorLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ErrorLogEntityMapper {
    ErrorLogEntity toEntity(ErrorLog domain);
    ErrorLog toDomain(ErrorLogEntity entity);
}
