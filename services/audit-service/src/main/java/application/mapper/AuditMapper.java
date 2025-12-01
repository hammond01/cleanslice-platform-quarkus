package application.mapper;

import application.dto.AuditEvent;
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
    AuditLog toEntity(AuditEvent event);
    
    AuditEvent toDto(AuditLog auditLog);
}
