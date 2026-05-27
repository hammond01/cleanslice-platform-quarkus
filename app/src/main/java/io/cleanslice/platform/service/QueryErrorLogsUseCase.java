package io.cleanslice.platform.service;
import io.cleanslice.platform.application.port.out.persistence.ErrorLogRepository;
import jakarta.inject.Inject;

import io.cleanslice.platform.domain.ErrorLog;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying error logs
 */
@ApplicationScoped
public class QueryErrorLogsUseCase {

    @Inject
    ErrorLogRepository errorLogRepository;

    public Uni<List<ErrorLog>> getAllLogs(int page, int size) {
        return errorLogRepository.getAllLogs(page, size);
    }

    public Uni<List<ErrorLog>> getLogsByExceptionType(String exceptionType, int page, int size) {
        return errorLogRepository.getLogsByExceptionType(exceptionType, page, size);
    }

    public Uni<List<ErrorLog>> getLogsByService(String serviceName, int page, int size) {
        return errorLogRepository.getLogsByService(serviceName, page, size);
    }

    public Uni<List<ErrorLog>> getLogsByUser(String userId, int page, int size) {
        return errorLogRepository.getLogsByUser(userId, page, size);
    }

    public Uni<List<ErrorLog>> getLogsByHttpMethod(String httpMethod, int page, int size) {
        return errorLogRepository.getLogsByHttpMethod(httpMethod, page, size);
    }

    public Uni<List<ErrorLog>> getLogsByCorrelationId(String correlationId) {
        return errorLogRepository.getLogsByCorrelationId(correlationId);
    }

    public Uni<List<ErrorLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return errorLogRepository.getLogsByDateRange(from, to, page, size);
    }

    public Uni<List<ErrorLog>> getRecentErrors(int limit) {
        return errorLogRepository.getRecentErrors(limit);
    }

    public Uni<List<ErrorLog>> searchLogs(String keyword, int page, int size) {
        return errorLogRepository.searchLogs(keyword, page, size);
    }

    public Uni<Long> countByExceptionType(String exceptionType) {
        return errorLogRepository.countByExceptionType(exceptionType);
    }

    public Uni<Long> countByService(String serviceName) {
        return errorLogRepository.countByService(serviceName);
    }

    public Uni<Long> countByHttpMethod(String httpMethod) {
        return errorLogRepository.countByHttpMethod(httpMethod);
    }
}

