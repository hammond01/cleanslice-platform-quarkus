package application.usecase;

import domain.entity.AuditLog;
import domain.entity.AuditType;
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

    @WithSession
    public Uni<List<AuditLog>> getAllLogs(int page, int size) {
        return AuditLog.findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByType(AuditType type, int page, int size) {
        return AuditLog.find("auditType = ?1", Sort.descending("timestamp"), type)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByUser(Long userId, int page, int size) {
        return AuditLog.find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByEntity(String entityType, Long entityId, int page, int size) {
        return AuditLog.find("entityType = ?1 and entityId = ?2",
                        Sort.descending("timestamp"), entityType, entityId)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByService(String serviceName, int page, int size) {
        return AuditLog.find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return AuditLog.find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AuditLog>> getLogsByCorrelationId(String correlationId) {
        return AuditLog.find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list();
    }

    @WithSession
    public Uni<List<AuditLog>> getRecentErrors(int limit) {
        return AuditLog.find("auditType = ?1", Sort.descending("timestamp"), AuditType.ERROR)
                .page(0, limit)
                .list();
    }

    @WithSession
    public Uni<List<AuditLog>> getRecentSecurityEvents(int limit) {
        return AuditLog.find("auditType = ?1", Sort.descending("timestamp"), AuditType.SECURITY)
                .page(0, limit)
                .list();
    }

    @WithSession
    public Uni<Long> countByType(AuditType type) {
        return AuditLog.count("auditType = ?1", type);
    }

    @WithSession
    public Uni<Long> countByUser(Long userId) {
        return AuditLog.count("userId = ?1", userId);
    }
}
