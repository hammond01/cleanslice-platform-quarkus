package infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import share.dto.*;
import share.enums.LogLevel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Centralized logging helper for publishing structured logs to Kafka
 * Provides simplified API for logging application events, errors, access, and performance
 */
@ApplicationScoped
public class LoggingHelper {

    @Inject
    @Channel("logs-application")
    Emitter<String> applicationLogEmitter;

    @Inject
    @Channel("logs-error")
    Emitter<String> errorLogEmitter;

    @Inject
    @Channel("logs-access")
    Emitter<String> accessLogEmitter;

    @Inject
    @Channel("logs-performance")
    Emitter<String> performanceLogEmitter;

    @Inject
    ObjectMapper objectMapper;

    private String serviceName;

    @PostConstruct
    void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        serviceName = System.getProperty("quarkus.application.name", "unknown-service");
    }

    public void logApp(LogLevel level, String message, String userId, String correlationId) {
        ApplicationLog log = new ApplicationLog();
        log.level = level;
        log.serviceName = serviceName;
        log.message = message;
        log.userId = userId;
        log.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        log.thread = Thread.currentThread().getName();
        publishLog(applicationLogEmitter, log);
    }

    public void logError(Throwable ex, String userId, String correlationId) {
        ErrorLog log = new ErrorLog();
        log.serviceName = serviceName;
        log.exceptionType = ex.getClass().getName();
        log.message = ex.getMessage();
        log.stackTrace = getStackTrace(ex);
        log.userId = userId;
        log.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publishLog(errorLogEmitter, log);
    }

    public void logAccess(String method, String endpoint, int status, long responseMs, String userId) {
        AccessLog log = new AccessLog();
        log.serviceName = serviceName;
        log.httpMethod = method;
        log.endpoint = endpoint;
        log.statusCode = status;
        log.responseTimeMs = responseMs;
        log.userId = userId;
        log.correlationId = UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publishLog(accessLogEmitter, log);
    }

    public void logPerf(String operation, long durationMs, boolean isSlow) {
        PerformanceLog log = new PerformanceLog();
        log.serviceName = serviceName;
        log.operation = operation;
        log.durationMs = durationMs;
        log.isSlow = isSlow;
        log.correlationId = UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publishLog(performanceLogEmitter, log);
    }

    private <T> void publishLog(Emitter<String> emitter, T log) {
        try {
            String json = objectMapper.writeValueAsString(log);
            Log.debugf("📤 Publishing log to Kafka: %s", log.getClass().getSimpleName());
            emitter.send(Message.of(json)
                .addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
                    .withKey(UUID.randomUUID().toString())
                    .build()));
            Log.debugf("✅ Successfully published %s to Kafka", log.getClass().getSimpleName());
        } catch (Exception e) {
            Log.errorf(e, "❌ Failed to publish %s to Kafka: %s", log.getClass().getSimpleName(), e.getMessage());
        }
    }

    private String getStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement el : ex.getStackTrace()) {
            sb.append(el.toString()).append("\n");
        }
        return sb.toString();
    }
}
