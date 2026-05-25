package io.cleanslice.platform.service;
import io.cleanslice.platform.infrastructure.persistence.entity.ApplicationLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.ApplicationLogEntityMapper;
import jakarta.inject.Inject;
import java.util.stream.Collectors;

import io.cleanslice.platform.domain.ApplicationLog;
import io.cleanslice.platform.domain.enums.LogLevel;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying application logs
 */
@ApplicationScoped
public class QueryApplicationLogsUseCase {

    @Inject
    ApplicationLogEntityMapper mapper;


    @WithSession
    public Uni<List<ApplicationLog>> getAllLogs(int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByLevel(LogLevel level, int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("level = ?1", Sort.descending("timestamp"), level)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByService(String serviceName, int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByUser(String userId, int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByCorrelationId(String correlationId) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<ApplicationLog>> searchLogs(String keyword, int page, int size) {
        String pattern = "%" + keyword + "%";
        return ApplicationLogEntity.<ApplicationLogEntity>find("message like ?1 or className like ?1",
                        Sort.descending("timestamp"), pattern)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<Long> countByLevel(LogLevel level) {
        return ApplicationLogEntity.count("level = ?1", level);
    }

    @WithSession
    public Uni<Long> countByService(String serviceName) {
        return ApplicationLogEntity.count("serviceName = ?1", serviceName);
    }
}


