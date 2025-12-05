package application.mapper;

import domain.entity.ApplicationLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface ApplicationLogMapper {
    ApplicationLog toEntity(share.dto.ApplicationLog dto);
}
