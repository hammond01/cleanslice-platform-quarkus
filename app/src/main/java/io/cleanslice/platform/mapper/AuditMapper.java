package io.cleanslice.platform.mapper;

import io.cleanslice.platform.dto.AuditEvent;
import io.cleanslice.platform.domain.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AuditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "auditTypeEnum", target = "auditType")
    AuditLog toEntity(AuditEvent event);

    @Mapping(source = "auditType", target = "auditTypeEnum")
    AuditEvent toDto(AuditLog auditLog);
}


