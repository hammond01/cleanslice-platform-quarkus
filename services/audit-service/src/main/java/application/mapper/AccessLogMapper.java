package application.mapper;

import domain.entity.AccessLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface AccessLogMapper {
    AccessLog toEntity(share.dto.AccessLog dto);
}
