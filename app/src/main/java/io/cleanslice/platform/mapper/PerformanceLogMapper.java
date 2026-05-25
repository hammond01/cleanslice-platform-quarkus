package io.cleanslice.platform.mapper;

import io.cleanslice.platform.domain.PerformanceLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import io.cleanslice.platform.dto.PerformanceLogDto;

import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PerformanceLogMapper {
    PerformanceLog toEntity(PerformanceLogDto dto);
}


