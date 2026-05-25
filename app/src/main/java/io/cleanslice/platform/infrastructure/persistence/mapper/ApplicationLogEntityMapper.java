package io.cleanslice.platform.infrastructure.persistence.mapper;

import io.cleanslice.platform.domain.ApplicationLog;
import io.cleanslice.platform.infrastructure.persistence.entity.ApplicationLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApplicationLogEntityMapper {
    ApplicationLogEntity toEntity(ApplicationLog domain);
    ApplicationLog toDomain(ApplicationLogEntity entity);
}
