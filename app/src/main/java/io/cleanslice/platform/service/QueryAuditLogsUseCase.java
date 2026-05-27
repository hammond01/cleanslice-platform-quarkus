package io.cleanslice.platform.service;
import io.cleanslice.platform.application.port.out.persistence.AuditLogRepository;
import jakarta.inject.Inject;

import io.cleanslice.platform.domain.AuditLog;
import io.cleanslice.platform.domain.enums.AuditTypeEnum;
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
    AuditLogRepository auditLogRepository;

    public Uni<List<AuditLog>> getAllLogs(int page, int size) {
        return auditLogRepository.getAllLogs(page, size);
    }

    public Uni<List<AuditLog>> getLogsByType(AuditTypeEnum type, int page, int size) {
        return auditLogRepository.getLogsByType(type, page, size);
    }

    public Uni<List<AuditLog>> getLogsByUser(Long userId, int page, int size) {
        return auditLogRepository.getLogsByUser(userId, page, size);
    }

    public Uni<List<AuditLog>> getLogsByEntity(String entityType, Long entityId, int page, int size) {
        return auditLogRepository.getLogsByEntity(entityType, entityId, page, size);
    }

    public Uni<List<AuditLog>> getLogsByService(String serviceName, int page, int size) {
        return auditLogRepository.getLogsByService(serviceName, page, size);
    }

    public Uni<List<AuditLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return auditLogRepository.getLogsByDateRange(from, to, page, size);
    }

    public Uni<List<AuditLog>> getLogsByCorrelationId(String correlationId) {
        return auditLogRepository.getLogsByCorrelationId(correlationId);
    }

    public Uni<List<AuditLog>> getRecentErrors(int limit) {
        return auditLogRepository.getRecentErrors(limit);
    }

    public Uni<List<AuditLog>> getRecentSecurityEvents(int limit) {
        return auditLogRepository.getRecentSecurityEvents(limit);
    }

    public Uni<Long> countByType(AuditTypeEnum type) {
        return auditLogRepository.countByType(type);
    }

    public Uni<Long> countByUser(Long userId) {
        return auditLogRepository.countByUser(userId);
    }
}


