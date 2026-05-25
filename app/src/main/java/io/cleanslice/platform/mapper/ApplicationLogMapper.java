package io.cleanslice.platform.mapper;

import io.cleanslice.platform.domain.ApplicationLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import io.cleanslice.platform.dto.ApplicationLogDto;

import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApplicationLogMapper {
    ApplicationLog toEntity(ApplicationLogDto dto);
}


