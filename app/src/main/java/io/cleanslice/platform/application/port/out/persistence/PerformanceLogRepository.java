package io.cleanslice.platform.application.port.out.persistence;

import io.cleanslice.platform.domain.PerformanceLog;
import io.smallrye.mutiny.Uni;

import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceLogRepository {
    Uni<PerformanceLog> save(PerformanceLog log);

    Uni<List<PerformanceLog>> getAllLogs(int page, int size);

    Uni<List<PerformanceLog>> getLogsByOperation(String operation, int page, int size);

    Uni<List<PerformanceLog>> getLogsByService(String serviceName, int page, int size);

    Uni<List<PerformanceLog>> getSlowOperations(int page, int size);

    Uni<List<PerformanceLog>> getLogsByMinDuration(Long minDuration, int page, int size);

    Uni<List<PerformanceLog>> getLogsByCorrelationId(String correlationId);

    Uni<List<PerformanceLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size);

    Uni<List<PerformanceLog>> searchLogs(String keyword, int page, int size);

    Uni<Long> countSlowOperations();

    Uni<Long> countByService(String serviceName);

    Uni<Double> getAverageDuration(String operation);
}
