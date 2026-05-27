package io.cleanslice.platform.service;
import io.cleanslice.platform.application.port.out.persistence.PerformanceLogRepository;

import jakarta.inject.Inject;

import io.cleanslice.platform.domain.PerformanceLog;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying performance logs (DB operations, timing)
 */
@ApplicationScoped
public class QueryPerformanceLogsUseCase {

    @Inject
    PerformanceLogRepository performanceLogRepository;

    public Uni<List<PerformanceLog>> getAllLogs(int page, int size) {
        return performanceLogRepository.getAllLogs(page, size);
    }

    public Uni<List<PerformanceLog>> getLogsByOperation(String operation, int page, int size) {
        return performanceLogRepository.getLogsByOperation(operation, page, size);
    }

    public Uni<List<PerformanceLog>> getLogsByService(String serviceName, int page, int size) {
        return performanceLogRepository.getLogsByService(serviceName, page, size);
    }

    public Uni<List<PerformanceLog>> getSlowOperations(int page, int size) {
        return performanceLogRepository.getSlowOperations(page, size);
    }

    public Uni<List<PerformanceLog>> getLogsByMinDuration(Long minDuration, int page, int size) {
        return performanceLogRepository.getLogsByMinDuration(minDuration, page, size);
    }

    public Uni<List<PerformanceLog>> getLogsByCorrelationId(String correlationId) {
        return performanceLogRepository.getLogsByCorrelationId(correlationId);
    }

    public Uni<List<PerformanceLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return performanceLogRepository.getLogsByDateRange(from, to, page, size);
    }

    public Uni<List<PerformanceLog>> searchLogs(String keyword, int page, int size) {
        return performanceLogRepository.searchLogs(keyword, page, size);
    }

    public Uni<Long> countSlowOperations() {
        return performanceLogRepository.countSlowOperations();
    }

    public Uni<Long> countByService(String serviceName) {
        return performanceLogRepository.countByService(serviceName);
    }

    public Uni<Double> getAverageDuration(String operation) {
        return performanceLogRepository.getAverageDuration(operation);
    }
}
