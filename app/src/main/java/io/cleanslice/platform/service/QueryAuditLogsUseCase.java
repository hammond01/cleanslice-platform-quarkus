package io.cleanslice.platform.service;
import io.cleanslice.platform.infrastructure.persistence.entity.AuditLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.AuditLogEntityMapper;
import jakarta.inject.Inject;
import java.util.stream.Collectors;

import io.cleanslice.platform.domain.AuditLog;
import io.cleanslice.platform.domain.enums.AuditTypeEnum;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying audit logs
 * Implements business logic for audit log queries
 */
@ApplicationScoped
public class QueryAuditLogsUseCase {

    @Inject
    AuditLogEntityMapper mapper;


    @WithSession
    public Uni<List<AuditLog>> getAllLogs(int page, int size) {
        return AuditLogEntity.<AuditLogEntity>findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByType(AuditTypeEnum type, int page, int size) {
        return AuditLogEntity.<AuditLogEntity>find("auditType = ?1", Sort.descending("timestamp"), type)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByUser(Long userId, int page, int size) {
        return AuditLogEntity.<AuditLogEntity>find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByEntity(String entityType, Long entityId, int page, int size) {
        return AuditLogEntity.<AuditLogEntity>find("entityType = ?1 and entityId = ?2",
                        Sort.descending("timestamp"), entityType, entityId)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByService(String serviceName, int page, int size) {
        return AuditLogEntity.<AuditLogEntity>find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return AuditLogEntity.<AuditLogEntity>find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByCorrelationId(String correlationId) {
        return AuditLogEntity.<AuditLogEntity>find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<AuditLog>> getRecentErrors(int limit) {
        return AuditLogEntity.<AuditLogEntity>find("auditType = ?1", Sort.descending("timestamp"), AuditTypeEnum.ERROR)
                .page(0, limit)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<List<AuditLog>> getRecentSecurityEvents(int limit) {
        return AuditLogEntity.<AuditLogEntity>find("auditType = ?1", Sort.descending("timestamp"), AuditTypeEnum.SECURITY)
                .page(0, limit)
                .list().onItem().transform(list -> list.stream().map(mapper::toDomain).collect(Collectors.toList()));
    }

    @WithSession
    public Uni<Long> countByType(AuditTypeEnum type) {
        return AuditLogEntity.count("auditType = ?1", type);
    }

    @WithSession
    public Uni<Long> countByUser(Long userId) {
        return AuditLogEntity.count("userId = ?1", userId);
    }
}


