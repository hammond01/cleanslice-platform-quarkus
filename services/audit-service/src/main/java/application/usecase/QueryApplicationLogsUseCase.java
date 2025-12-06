package application.usecase;

import domain.entity.ApplicationLog;
import share.enums.LogLevel;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Use case for querying application logs
 */
@ApplicationScoped
public class QueryApplicationLogsUseCase {

    @WithSession
    public Uni<List<ApplicationLog>> getAllLogs(int page, int size) {
        return ApplicationLog.findAll(Sort.descending("timestamp"))
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByLevel(LogLevel level, int page, int size) {
        return ApplicationLog.find("level = ?1", Sort.descending("timestamp"), level)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByService(String serviceName, int page, int size) {
        return ApplicationLog.find("serviceName = ?1", Sort.descending("timestamp"), serviceName)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByUser(String userId, int page, int size) {
        return ApplicationLog.find("userId = ?1", Sort.descending("timestamp"), userId)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByCorrelationId(String correlationId) {
        return ApplicationLog.find("correlationId = ?1", Sort.ascending("timestamp"), correlationId)
                .list();
    }

    @WithSession
    public Uni<List<ApplicationLog>> getLogsByDateRange(LocalDateTime from, LocalDateTime to, int page, int size) {
        return ApplicationLog.find("timestamp >= ?1 and timestamp <= ?2",
                        Sort.descending("timestamp"), from, to)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<List<ApplicationLog>> searchLogs(String keyword, int page, int size) {
        String pattern = "%" + keyword + "%";
        return ApplicationLog.find("message like ?1 or eventType like ?1",
                        Sort.descending("timestamp"), pattern)
                .page(page, size)
                .list();
    }

    @WithSession
    public Uni<Long> countByLevel(LogLevel level) {
        return ApplicationLog.count("level = ?1", level);
    }

    @WithSession
    public Uni<Long> countByService(String serviceName) {
        return ApplicationLog.count("serviceName = ?1", serviceName);
    }
}
