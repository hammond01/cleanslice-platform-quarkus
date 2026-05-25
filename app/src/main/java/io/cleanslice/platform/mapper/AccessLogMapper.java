package io.cleanslice.platform.mapper;

import io.cleanslice.platform.domain.AccessLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import io.cleanslice.platform.dto.AccessLogDto;

import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA_CDI, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccessLogMapper {
    AccessLog toEntity(AccessLogDto dto);
}


