package io.cleanslice.platform.service;
import io.cleanslice.platform.application.port.out.persistence.ApplicationLogRepository;
import jakarta.inject.Inject;

import io.cleanslice.platform.domain.ApplicationLog;
import io.cleanslice.platform.domain.enums.LogLevel;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying application logs
 */
@ApplicationScoped
public class QueryApplicationLogsUseCase {

    @Inject
    ApplicationLogRepository applicationLogRepository;

    public Uni<List<ApplicationLog>> getAllLogs(int page, int size) {
        return applicationLogRepository.getAllLogs(page, size);
    }

    public Uni<List<ApplicationLog>> getLogsByLevel(LogLevel level, int page, int size) {
        return applicationLogRepository.getLogsByLevel(level, page, size);
    }

    public Uni<List<ApplicationLog>> getLogsByService(String serviceName, int page, int size) {
        return applicationLogRepository.getLogsByService(serviceName, page, size);
    }

    public Uni<List<ApplicationLog>> getLogsByUser(String userId, int page, int size) {
        return applicationLogRepository.getLogsByUser(userId, page, size);
    }

    public Uni<List<ApplicationLog>> getLogsByCorrelationId(String correlationId) {
        return applicationLogRepository.getLogsByCorrelationId(correlationId);
    }

    public Uni<List<ApplicationLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return applicationLogRepository.getLogsByDateRange(from, to, page, size);
    }

    public Uni<List<ApplicationLog>> searchLogs(String keyword, int page, int size) {
        return applicationLogRepository.searchLogs(keyword, page, size);
    }

    public Uni<Long> countByLevel(LogLevel level) {
        return applicationLogRepository.countByLevel(level);
    }

    public Uni<Long> countByService(String serviceName) {
        return applicationLogRepository.countByService(serviceName);
    }
}


