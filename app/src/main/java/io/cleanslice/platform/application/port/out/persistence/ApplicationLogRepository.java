package io.cleanslice.platform.application.port.out.persistence;

import io.cleanslice.platform.domain.ApplicationLog;
import io.cleanslice.platform.domain.enums.LogLevel;
import io.smallrye.mutiny.Uni;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationLogRepository {
    Uni<ApplicationLog> save(ApplicationLog log);

    Uni<List<ApplicationLog>> getAllLogs(int page, int size);

    Uni<List<ApplicationLog>> getLogsByLevel(LogLevel level, int page, int size);

    Uni<List<ApplicationLog>> getLogsByService(String serviceName, int page, int size);

    Uni<List<ApplicationLog>> getLogsByUser(String userId, int page, int size);

    Uni<List<ApplicationLog>> getLogsByCorrelationId(String correlationId);

    Uni<List<ApplicationLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size);

    Uni<List<ApplicationLog>> searchLogs(String keyword, int page, int size);

    Uni<Long> countByLevel(LogLevel level);

    Uni<Long> countByService(String serviceName);
}
