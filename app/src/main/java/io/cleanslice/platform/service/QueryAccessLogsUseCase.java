package io.cleanslice.platform.service;
import io.cleanslice.platform.application.port.out.persistence.AccessLogRepository;
import jakarta.inject.Inject;

import io.cleanslice.platform.domain.AccessLog;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying access logs (HTTP requests/responses)
 */
@ApplicationScoped
public class QueryAccessLogsUseCase {

    @Inject
    AccessLogRepository accessLogRepository;

    public Uni<List<AccessLog>> getAllLogs(int page, int size) {
        return accessLogRepository.getAllLogs(page, size);
    }

    public Uni<List<AccessLog>> getLogsByMethod(String httpMethod, int page, int size) {
        return accessLogRepository.getLogsByMethod(httpMethod, page, size);
    }

    public Uni<List<AccessLog>> getLogsByEndpoint(String endpoint, int page, int size) {
        return accessLogRepository.getLogsByEndpoint(endpoint, page, size);
    }

    public Uni<List<AccessLog>> getLogsByStatusCode(Integer statusCode, int page, int size) {
        return accessLogRepository.getLogsByStatusCode(statusCode, page, size);
    }

    public Uni<List<AccessLog>> getLogsByService(String serviceName, int page, int size) {
        return accessLogRepository.getLogsByService(serviceName, page, size);
    }

    public Uni<List<AccessLog>> getLogsByUser(String userId, int page, int size) {
        return accessLogRepository.getLogsByUser(userId, page, size);
    }

    public Uni<List<AccessLog>> getLogsByCorrelationId(String correlationId) {
        return accessLogRepository.getLogsByCorrelationId(correlationId);
    }

    public Uni<List<AccessLog>> getSlowRequests(Long minResponseTime, int page, int size) {
        return accessLogRepository.getSlowRequests(minResponseTime, page, size);
    }

    public Uni<List<AccessLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return accessLogRepository.getLogsByDateRange(from, to, page, size);
    }

    public Uni<List<AccessLog>> getErrorResponses(int page, int size) {
        return accessLogRepository.getErrorResponses(page, size);
    }

    public Uni<Long> countByStatusCode(Integer statusCode) {
        return accessLogRepository.countByStatusCode(statusCode);
    }

    public Uni<Long> countByMethod(String httpMethod) {
        return accessLogRepository.countByMethod(httpMethod);
    }

    public Uni<Long> countSlowRequests(Long minResponseTime) {
        return accessLogRepository.countSlowRequests(minResponseTime);
    }
}

