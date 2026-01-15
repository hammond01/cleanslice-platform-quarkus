package application.mapper;

import share.dto.AuditEvent;
import domain.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AuditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "auditTypeEnum", target = "auditType")
    AuditLog toEntity(AuditEvent event);

    @Mapping(source = "auditType", target = "auditTypeEnum")
    AuditEvent toDto(AuditLog auditLog);
}
