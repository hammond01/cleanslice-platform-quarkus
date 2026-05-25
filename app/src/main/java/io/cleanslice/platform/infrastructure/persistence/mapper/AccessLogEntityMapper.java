package io.cleanslice.platform.infrastructure.persistence.mapper;

import io.cleanslice.platform.domain.AccessLog;
import io.cleanslice.platform.infrastructure.persistence.entity.AccessLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccessLogEntityMapper {
    AccessLogEntity toEntity(AccessLog domain);
    AccessLog toDomain(AccessLogEntity entity);
}
