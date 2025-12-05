package application.mapper;

import domain.entity.PerformanceLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI)
public interface PerformanceLogMapper {
    PerformanceLog toEntity(share.dto.PerformanceLog dto);
}
