package io.cleanslice.platform.application.port.out.persistence;

import io.cleanslice.platform.domain.AccessLog;
import io.smallrye.mutiny.Uni;

import java.time.LocalDateTime;
import java.util.List;

public interface AccessLogRepository {
    Uni<AccessLog> save(AccessLog log);

    Uni<List<AccessLog>> getAllLogs(int page, int size);

    Uni<List<AccessLog>> getLogsByMethod(String httpMethod, int page, int size);

    Uni<List<AccessLog>> getLogsByEndpoint(String endpoint, int page, int size);

    Uni<List<AccessLog>> getLogsByStatusCode(Integer statusCode, int page, int size);

    Uni<List<AccessLog>> getLogsByService(String serviceName, int page, int size);

    Uni<List<AccessLog>> getLogsByUser(String userId, int page, int size);

    Uni<List<AccessLog>> getLogsByCorrelationId(String correlationId);

    Uni<List<AccessLog>> getSlowRequests(Long minResponseTime, int page, int size);

    Uni<List<AccessLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size);

    Uni<List<AccessLog>> getErrorResponses(int page, int size);

    Uni<Long> countByStatusCode(Integer statusCode);

    Uni<Long> countByMethod(String httpMethod);

    Uni<Long> countSlowRequests(Long minResponseTime);
}
