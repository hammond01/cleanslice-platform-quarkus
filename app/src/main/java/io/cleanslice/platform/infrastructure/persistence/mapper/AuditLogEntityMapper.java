package io.cleanslice.platform.infrastructure.persistence.mapper;

import io.cleanslice.platform.domain.AuditLog;
import io.cleanslice.platform.infrastructure.persistence.entity.AuditLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuditLogEntityMapper {
    AuditLogEntity toEntity(AuditLog domain);
    AuditLog toDomain(AuditLogEntity entity);
}
