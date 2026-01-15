package application.usecase;

import domain.entity.AccessLog;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying access logs (HTTP requests/responses)
 */
@ApplicationScoped
public class QueryAccessLogsUseCase {

    @WithSession
    public Uni<List<AccessLog>> getAllLogs(int page, int size) {
        return AccessLog.findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getLogsByMethod(String httpMethod, int page, int size) {
        return AccessLog.find("httpMethod = ?1", Sort.descending("timestamp"), httpMethod)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getLogsByEndpoint(String endpoint, int page, int size) {
        return AccessLog.find("endpoint = ?1", Sort.descending("timestamp"), endpoint)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getLogsByStatusCode(Integer statusCode, int page, int size) {
        return AccessLog.find("statusCode = ?1", Sort.descending("timestamp"), statusCode)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getLogsByService(String serviceName, int page, int size) {
        return AccessLog.find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getLogsByUser(String userId, int page, int size) {
        return AccessLog.find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getLogsByCorrelationId(String correlationId) {
        return AccessLog.find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getSlowRequests(Long minResponseTime, int page, int size) {
        return AccessLog.find("responseTimeMs >= ?1", Sort.descending("responseTimeMs"), minResponseTime)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return AccessLog.find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<AccessLog>> getErrorResponses(int page, int size) {
        return AccessLog.find("statusCode >= 400", Sort.descending("timestamp"))
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<Long> countByStatusCode(Integer statusCode) {
        return AccessLog.count("statusCode = ?1", statusCode);
    }

    @WithSession
    public Uni<Long> countByMethod(String httpMethod) {
        return AccessLog.count("httpMethod = ?1", httpMethod);
    }

    @WithSession
    public Uni<Long> countSlowRequests(Long minResponseTime) {
        return AccessLog.count("responseTimeMs >= ?1", minResponseTime);
    }
}
