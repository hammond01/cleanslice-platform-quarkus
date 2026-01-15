package application.mapper;

import domain.entity.ErrorLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface ErrorLogMapper {
    ErrorLog toEntity(share.dto.ErrorLog dto);
}
