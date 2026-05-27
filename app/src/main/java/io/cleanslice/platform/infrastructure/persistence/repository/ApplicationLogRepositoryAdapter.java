package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.application.port.out.persistence.ApplicationLogRepository;
import io.cleanslice.platform.domain.ApplicationLog;
import io.cleanslice.platform.domain.enums.LogLevel;
import io.cleanslice.platform.infrastructure.persistence.entity.ApplicationLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.ApplicationLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ApplicationLogRepositoryAdapter implements ApplicationLogRepository {

    @Inject
    ApplicationLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<ApplicationLog> save(ApplicationLog log) {
        ApplicationLogEntity entity = mapper.toEntity(log);
        return ApplicationLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }

    @Override
    @WithSession
    public Uni<List<ApplicationLog>> getAllLogs(int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ApplicationLog>> getLogsByLevel(LogLevel level, int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("level = ?1", Sort.descending("timestamp"), level)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ApplicationLog>> getLogsByService(String serviceName, int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ApplicationLog>> getLogsByUser(String userId, int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ApplicationLog>> getLogsByCorrelationId(String correlationId) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ApplicationLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return ApplicationLogEntity.<ApplicationLogEntity>find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ApplicationLog>> searchLogs(String keyword, int page, int size) {
        String pattern = "%" + keyword + "%";
        return ApplicationLogEntity.<ApplicationLogEntity>find("message like ?1 or className like ?1",
                        Sort.descending("timestamp"), pattern)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<Long> countByLevel(LogLevel level) {
        return ApplicationLogEntity.count("level = ?1", level);
    }

    @Override
    @WithSession
    public Uni<Long> countByService(String serviceName) {
        return ApplicationLogEntity.count("serviceName = ?1", serviceName);
    }
}
