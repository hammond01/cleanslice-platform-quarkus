package io.cleanslice.platform.service;

import io.cleanslice.platform.infrastructure.persistence.entity.PerformanceLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.PerformanceLogEntityMapper;
import jakarta.inject.Inject;
import java.util.stream.Collectors;

import io.cleanslice.platform.domain.PerformanceLog;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying performance logs (DB operations, timing)
 */
@ApplicationScoped
public class QueryPerformanceLogsUseCase {

    @Inject
    PerformanceLogEntityMapper mapper;

    @WithSession
    public Uni<List<PerformanceLog>> getAllLogs(int page, int size) {
        return PerformanceLogEntity.<PerformanceLogEntity>findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<PerformanceLog>> getLogsByOperation(String operation, int page, int size) {
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("operation = ?1", Sort.descending("timestamp"), operation)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<PerformanceLog>> getLogsByService(String serviceName, int page, int size) {
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<PerformanceLog>> getSlowOperations(int page, int size) {
        return PerformanceLogEntity.<PerformanceLogEntity>find("isSlow = true", Sort.descending("durationMs"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<PerformanceLog>> getLogsByMinDuration(Long minDuration, int page, int size) {
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("durationMs >= ?1", Sort.descending("durationMs"), minDuration)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<PerformanceLog>> getLogsByCorrelationId(String correlationId) {
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<PerformanceLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return PerformanceLogEntity.<PerformanceLogEntity>find("timestamp >= ?1 and timestamp <= ?2",
                Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<PerformanceLog>> searchLogs(String keyword, int page, int size) {
        String pattern = "%" + keyword + "%";
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("operation like ?1", Sort.descending("timestamp"), pattern)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<Long> countSlowOperations() {
        return PerformanceLogEntity.count("isSlow = true");
    }

    @WithSession
    public Uni<Long> countByService(String serviceName) {
        return PerformanceLogEntity.count("serviceName = ?1", serviceName);
    }

    @WithSession
    public Uni<Double> getAverageDuration(String operation) {
        return PerformanceLogEntity.<PerformanceLogEntity>find("operation = ?1", operation)
                .list()
                .onItem().transform(logs -> {
                    if (logs.isEmpty())
                        return 0.0;
                    long sum = logs.stream()
                            .mapToLong(log -> log.durationMs != null ? log.durationMs : 0)
                            .sum();
                    return (double) sum / logs.size();
                });
    }
}