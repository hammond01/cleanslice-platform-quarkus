package io.cleanslice.platform.infrastructure.persistence.mapper;

import io.cleanslice.platform.domain.PerformanceLog;
import io.cleanslice.platform.infrastructure.persistence.entity.PerformanceLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PerformanceLogEntityMapper {
    PerformanceLogEntity toEntity(PerformanceLog domain);
    PerformanceLog toDomain(PerformanceLogEntity entity);
}
