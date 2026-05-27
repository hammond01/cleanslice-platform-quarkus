package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.application.port.out.persistence.PerformanceLogRepository;
import io.cleanslice.platform.domain.PerformanceLog;
import io.cleanslice.platform.infrastructure.persistence.entity.PerformanceLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.PerformanceLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PerformanceLogRepositoryAdapter implements PerformanceLogRepository {

    @Inject
    PerformanceLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<PerformanceLog> save(PerformanceLog log) {
        PerformanceLogEntity entity = mapper.toEntity(log);
        return PerformanceLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }

    @Override
    @WithSession
    public Uni<List<PerformanceLog>> getAllLogs(int page, int size) {
        return PerformanceLogEntity.<PerformanceLogEntity>findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<PerformanceLog>> getLogsByOperation(String operation, int page, int size) {
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("operation = ?1", Sort.descending("timestamp"), operation)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<PerformanceLog>> getLogsByService(String serviceName, int page, int size) {
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<PerformanceLog>> getSlowOperations(int page, int size) {
        return PerformanceLogEntity.<PerformanceLogEntity>find("isSlow = true", Sort.descending("durationMs"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<PerformanceLog>> getLogsByMinDuration(Long minDuration, int page, int size) {
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("durationMs >= ?1", Sort.descending("durationMs"), minDuration)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<PerformanceLog>> getLogsByCorrelationId(String correlationId) {
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<PerformanceLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return PerformanceLogEntity.<PerformanceLogEntity>find("timestamp >= ?1 and timestamp <= ?2",
                Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<PerformanceLog>> searchLogs(String keyword, int page, int size) {
        String pattern = "%" + keyword + "%";
        return PerformanceLogEntity
                .<PerformanceLogEntity>find("operation like ?1", Sort.descending("timestamp"), pattern)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<Long> countSlowOperations() {
        return PerformanceLogEntity.count("isSlow = true");
    }

    @Override
    @WithSession
    public Uni<Long> countByService(String serviceName) {
        return PerformanceLogEntity.count("serviceName = ?1", serviceName);
    }

    @Override
    @WithSession
    public Uni<Double> getAverageDuration(String operation) {
        return PerformanceLogEntity.<PerformanceLogEntity>find("operation = ?1", operation)
                .list()
                .onItem().transform(logs -> {
                    if (logs.isEmpty()) {
                        return 0.0;
                    }
                    long sum = logs.stream()
                            .mapToLong(log -> log.durationMs != null ? log.durationMs : 0)
                            .sum();
                    return (double) sum / logs.size();
                });
    }
}
