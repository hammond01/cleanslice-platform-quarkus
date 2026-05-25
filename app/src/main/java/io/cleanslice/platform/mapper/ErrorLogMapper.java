package io.cleanslice.platform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import io.cleanslice.platform.domain.ErrorLog;

import io.cleanslice.platform.dto.ErrorLogDto;

import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ErrorLogMapper {
    ErrorLog toEntity(ErrorLogDto dto);
}



