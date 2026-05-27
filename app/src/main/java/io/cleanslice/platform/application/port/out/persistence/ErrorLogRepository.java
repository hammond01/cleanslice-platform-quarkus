package io.cleanslice.platform.application.port.out.persistence;

import io.cleanslice.platform.domain.ErrorLog;
import io.smallrye.mutiny.Uni;

import java.time.LocalDateTime;
import java.util.List;

public interface ErrorLogRepository {
    Uni<ErrorLog> save(ErrorLog log);

    Uni<List<ErrorLog>> getAllLogs(int page, int size);

    Uni<List<ErrorLog>> getLogsByExceptionType(String exceptionType, int page, int size);

    Uni<List<ErrorLog>> getLogsByService(String serviceName, int page, int size);

    Uni<List<ErrorLog>> getLogsByUser(String userId, int page, int size);

    Uni<List<ErrorLog>> getLogsByHttpMethod(String httpMethod, int page, int size);

    Uni<List<ErrorLog>> getLogsByCorrelationId(String correlationId);

    Uni<List<ErrorLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size);

    Uni<List<ErrorLog>> getRecentErrors(int limit);

    Uni<List<ErrorLog>> searchLogs(String keyword, int page, int size);

    Uni<Long> countByExceptionType(String exceptionType);

    Uni<Long> countByService(String serviceName);

    Uni<Long> countByHttpMethod(String httpMethod);
}
