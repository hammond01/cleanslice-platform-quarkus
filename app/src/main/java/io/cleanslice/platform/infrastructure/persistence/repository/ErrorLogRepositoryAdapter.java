package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.application.port.out.persistence.ErrorLogRepository;
import io.cleanslice.platform.domain.ErrorLog;
import io.cleanslice.platform.infrastructure.persistence.entity.ErrorLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.ErrorLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ErrorLogRepositoryAdapter implements ErrorLogRepository {

    @Inject
    ErrorLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<ErrorLog> save(ErrorLog log) {
        ErrorLogEntity entity = mapper.toEntity(log);
        return ErrorLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> getAllLogs(int page, int size) {
        return ErrorLogEntity.<ErrorLogEntity>findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> getLogsByExceptionType(String exceptionType, int page, int size) {
        return ErrorLogEntity.<ErrorLogEntity>find("exceptionType = ?1", Sort.descending("timestamp"), exceptionType)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> getLogsByService(String serviceName, int page, int size) {
        return ErrorLogEntity.<ErrorLogEntity>find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> getLogsByUser(String userId, int page, int size) {
        return ErrorLogEntity.<ErrorLogEntity>find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> getLogsByHttpMethod(String httpMethod, int page, int size) {
        return ErrorLogEntity.<ErrorLogEntity>find("httpMethod = ?1", Sort.descending("timestamp"), httpMethod)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> getLogsByCorrelationId(String correlationId) {
        return ErrorLogEntity.<ErrorLogEntity>find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return ErrorLogEntity.<ErrorLogEntity>find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> getRecentErrors(int limit) {
        return ErrorLogEntity.<ErrorLogEntity>findAll(Sort.descending("timestamp"))
                .page(0, limit)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<List<ErrorLog>> searchLogs(String keyword, int page, int size) {
        String pattern = "%" + keyword + "%";
        return ErrorLogEntity.<ErrorLogEntity>find("message like ?1 or exceptionType like ?1 or stackTrace like ?1",
                        Sort.descending("timestamp"), pattern)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @Override
    @WithSession
    public Uni<Long> countByExceptionType(String exceptionType) {
        return ErrorLogEntity.count("exceptionType = ?1", exceptionType);
    }

    @Override
    @WithSession
    public Uni<Long> countByService(String serviceName) {
        return ErrorLogEntity.count("serviceName = ?1", serviceName);
    }

    @Override
    @WithSession
    public Uni<Long> countByHttpMethod(String httpMethod) {
        return ErrorLogEntity.count("httpMethod = ?1", httpMethod);
    }
}
