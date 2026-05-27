package io.cleanslice.platform.application.port.out.persistence;

import io.cleanslice.platform.domain.AuditLog;
import io.cleanslice.platform.domain.enums.AuditTypeEnum;
import io.smallrye.mutiny.Uni;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository {
    Uni<AuditLog> save(AuditLog log);

    Uni<List<AuditLog>> getAllLogs(int page, int size);

    Uni<List<AuditLog>> getLogsByType(AuditTypeEnum type, int page, int size);

    Uni<List<AuditLog>> getLogsByUser(Long userId, int page, int size);

    Uni<List<AuditLog>> getLogsByEntity(String entityType, Long entityId, int page, int size);

    Uni<List<AuditLog>> getLogsByService(String serviceName, int page, int size);

    Uni<List<AuditLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size);

    Uni<List<AuditLog>> getLogsByCorrelationId(String correlationId);

    Uni<List<AuditLog>> getRecentErrors(int limit);

    Uni<List<AuditLog>> getRecentSecurityEvents(int limit);

    Uni<Long> countByType(AuditTypeEnum type);

    Uni<Long> countByUser(Long userId);
}
