package application.usecase;

import domain.entity.ErrorLog;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying error logs
 */
@ApplicationScoped
public class QueryErrorLogsUseCase {

    @WithSession
    public Uni<List<ErrorLog>> getAllLogs(int page, int size) {
        return ErrorLog.findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ErrorLog>> getLogsByExceptionType(String exceptionType, int page, int size) {
        return ErrorLog.find("exceptionType = ?1", Sort.descending("timestamp"), exceptionType)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ErrorLog>> getLogsByService(String serviceName, int page, int size) {
        return ErrorLog.find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ErrorLog>> getLogsByUser(String userId, int page, int size) {
        return ErrorLog.find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ErrorLog>> getLogsByHttpStatus(Integer httpStatus, int page, int size) {
        return ErrorLog.find("httpStatus = ?1", Sort.descending("timestamp"), httpStatus)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ErrorLog>> getLogsByCorrelationId(String correlationId) {
        return ErrorLog.find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list();
    }

    @WithSession
    public Uni<List<ErrorLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return ErrorLog.find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ErrorLog>> getRecentErrors(int limit) {
        return ErrorLog.findAll(Sort.descending("timestamp"))
                .page(0, limit)
                .list();
    }

    @WithSession
    public Uni<List<ErrorLog>> searchLogs(String keyword, int page, int size) {
        String pattern = "%" + keyword + "%";
        return ErrorLog.find("message like ?1 or exceptionType like ?1 or stackTrace like ?1",
                        Sort.descending("timestamp"), pattern)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<Long> countByExceptionType(String exceptionType) {
        return ErrorLog.count("exceptionType = ?1", exceptionType);
    }

    @WithSession
    public Uni<Long> countByService(String serviceName) {
        return ErrorLog.count("serviceName = ?1", serviceName);
    }

    @WithSession
    public Uni<Long> countByHttpStatus(Integer httpStatus) {
        return ErrorLog.count("httpStatus = ?1", httpStatus);
    }
}
