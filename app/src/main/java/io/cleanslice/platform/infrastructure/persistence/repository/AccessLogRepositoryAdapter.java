package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.application.port.out.persistence.AccessLogRepository;
import io.cleanslice.platform.domain.AccessLog;
import io.cleanslice.platform.infrastructure.persistence.entity.AccessLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.AccessLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AccessLogRepositoryAdapter implements AccessLogRepository {

    @Inject
    AccessLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<AccessLog> save(AccessLog log) {
        AccessLogEntity entity = mapper.toEntity(log);
        return AccessLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getAllLogs(int page, int size) {
        return AccessLogEntity.<AccessLogEntity>findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getLogsByMethod(String httpMethod, int page, int size) {
        return AccessLogEntity.<AccessLogEntity>find("httpMethod = ?1", Sort.descending("timestamp"), httpMethod)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getLogsByEndpoint(String endpoint, int page, int size) {
        return AccessLogEntity.<AccessLogEntity>find("endpoint = ?1", Sort.descending("timestamp"), endpoint)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getLogsByStatusCode(Integer statusCode, int page, int size) {
        return AccessLogEntity.<AccessLogEntity>find("statusCode = ?1", Sort.descending("timestamp"), statusCode)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getLogsByService(String serviceName, int page, int size) {
        return AccessLogEntity.<AccessLogEntity>find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getLogsByUser(String userId, int page, int size) {
        return AccessLogEntity.<AccessLogEntity>find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getLogsByCorrelationId(String correlationId) {
        return AccessLogEntity.<AccessLogEntity>find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getSlowRequests(Long minResponseTime, int page, int size) {
        return AccessLogEntity.<AccessLogEntity>find("responseTimeMs >= ?1", Sort.descending("responseTimeMs"), minResponseTime)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return AccessLogEntity.<AccessLogEntity>find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<AccessLog>> getErrorResponses(int page, int size) {
        return AccessLogEntity.<AccessLogEntity>find("statusCode >= 400", Sort.descending("timestamp"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<Long> countByStatusCode(Integer statusCode) {
        return AccessLogEntity.count("statusCode = ?1", statusCode);
    }

    @Override
    @WithSession
    public Uni<Long> countByMethod(String httpMethod) {
        return AccessLogEntity.count("httpMethod = ?1", httpMethod);
    }

    @Override
    @WithSession
    public Uni<Long> countSlowRequests(Long minResponseTime) {
        return AccessLogEntity.count("responseTimeMs >= ?1", minResponseTime);
    }
}
