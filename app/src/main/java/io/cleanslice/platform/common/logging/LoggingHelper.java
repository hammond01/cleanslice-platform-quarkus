package io.cleanslice.platform.common.logging;

import io.cleanslice.platform.service.ProcessAccessLogUseCase;
import io.cleanslice.platform.service.ProcessApplicationLogUseCase;
import io.cleanslice.platform.service.ProcessErrorLogUseCase;
import io.cleanslice.platform.service.ProcessPerformanceLogUseCase;
import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.smallrye.mutiny.Uni;
import io.cleanslice.platform.dto.AccessLogDto;
import io.cleanslice.platform.dto.ApplicationLogDto;
import io.cleanslice.platform.dto.ErrorLogDto;
import io.cleanslice.platform.dto.PerformanceLogDto;
import io.cleanslice.platform.domain.enums.LogLevel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Centralized logging helper for persisting structured logs in-process
 */
@ApplicationScoped
public class LoggingHelper {

    @Inject
    ProcessApplicationLogUseCase processApplicationLogUseCase;

    @Inject
    ProcessErrorLogUseCase processErrorLogUseCase;

    @Inject
    ProcessAccessLogUseCase processAccessLogUseCase;

    @Inject
    ProcessPerformanceLogUseCase processPerformanceLogUseCase;

    private String serviceName;

    @PostConstruct
    void init() {
        serviceName = System.getProperty("quarkus.application.name", "unknown-service");
    }

    public void logApp(LogLevel level, String message, String userId, String correlationId) {
        ApplicationLogDto log = new ApplicationLogDto();
        log.level = level;
        log.serviceName = serviceName;
        log.message = message;
        log.userId = userId;
        log.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        log.thread = Thread.currentThread().getName();
        publish("ApplicationLog", processApplicationLogUseCase.process(log));
    }

    public void logError(Throwable ex, String userId, String correlationId) {
        ErrorLogDto log = new ErrorLogDto();
        log.serviceName = serviceName;
        log.exceptionType = ex.getClass().getName();
        log.message = ex.getMessage();
        log.stackTrace = getStackTrace(ex);
        log.userId = userId;
        log.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publish("ErrorLog", processErrorLogUseCase.process(log));
    }

    public void logAccess(String method, String endpoint, int status, long responseMs, String userId) {
        AccessLogDto log = new AccessLogDto();
        log.serviceName = serviceName;
        log.httpMethod = method;
        log.endpoint = endpoint;
        log.statusCode = status;
        log.responseTimeMs = responseMs;
        log.userId = userId;
        log.correlationId = UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publish("AccessLog", processAccessLogUseCase.process(log));
    }

    public void logPerf(String operation, long durationMs, boolean isSlow) {
        PerformanceLogDto log = new PerformanceLogDto();
        log.serviceName = serviceName;
        log.operation = operation;
        log.durationMs = durationMs;
        log.isSlow = isSlow;
        log.correlationId = UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publish("PerformanceLog", processPerformanceLogUseCase.process(log));
    }

    private void publish(String logType, Uni<Void> operation) {
        operation.subscribe().with(
            unused -> Log.debugf("Saved %s in-process", logType),
            failure -> Log.errorf(failure, "Failed to save %s in-process", logType)
        );
    }

    private String getStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement el : ex.getStackTrace()) {
            sb.append(el.toString()).append("\n");
        }
        return sb.toString();
    }
}


