package infrastructure.messaging.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import share.dto.AuditEvent;
import application.port.inbound.AuditEventConsumerPort;
import application.usecase.*;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Kafka adapter for consuming all log types
 * Handles both audit events and general logs (application, error, access, performance)
 */
@ApplicationScoped
public class KafkaAuditEventConsumerAdapter {

    private static final Logger LOG = Logger.getLogger(KafkaAuditEventConsumerAdapter.class);

    @Inject
    ObjectMapper objectMapper;
    
    @Inject
    AuditEventConsumerPort processAuditEventUseCase;
    
    @Inject
    ProcessApplicationLogUseCase processApplicationLogUseCase;
    
    @Inject
    ProcessErrorLogUseCase processErrorLogUseCase;
    
    @Inject
    ProcessAccessLogUseCase processAccessLogUseCase;
    
    @Inject
    ProcessPerformanceLogUseCase processPerformanceLogUseCase;

    @Incoming("login-events")
    public Uni<Void> consumeLoginEvent(String message) {
        return processEvent(message, "login-events");
    }

    @Incoming("crud-events")
    public Uni<Void> consumeCrudEvent(String message) {
        return processEvent(message, "crud-events");
    }

    @Incoming("transaction-events")
    public Uni<Void> consumeTransactionEvent(String message) {
        return processEvent(message, "transaction-events");
    }

    @Incoming("security-events")
    public Uni<Void> consumeSecurityEvent(String message) {
        return processEvent(message, "security-events");
    }

    @Incoming("system-events")
    public Uni<Void> consumeSystemEvent(String message) {
        return processEvent(message, "system-events");
    }

    @Incoming("error-events")
    public Uni<Void> consumeErrorEvent(String message) {
        return processEvent(message, "error-events");
    }
    
    // === New log types consumers ===
    
    @Incoming("application-logs")
    public Uni<Void> consumeApplicationLog(String message) {
        return processLog(message, "application-logs", share.dto.ApplicationLog.class, 
            processApplicationLogUseCase::process);
    }
    
    @Incoming("error-logs")
    public Uni<Void> consumeErrorLog(String message) {
        return processLog(message, "error-logs", share.dto.ErrorLog.class, 
            processErrorLogUseCase::process);
    }
    
    @Incoming("access-logs")
    public Uni<Void> consumeAccessLog(String message) {
        return processLog(message, "access-logs", share.dto.AccessLog.class, 
            processAccessLogUseCase::process);
    }
    
    @Incoming("performance-logs")
    public Uni<Void> consumePerformanceLog(String message) {
        return processLog(message, "performance-logs", share.dto.PerformanceLog.class, 
            processPerformanceLogUseCase::process);
    }

    private Uni<Void> processEvent(String message, String topic) {
        try {
            LOG.infof("📩 Received audit event from %s", topic);
            AuditEvent event = objectMapper.readValue(message, AuditEvent.class);
            return processAuditEventUseCase.processAuditEvent(event)
                .onItem().invoke(() -> LOG.infof("✅ Successfully processed audit event from %s: %s %s", 
                    topic, event.action, event.entityType))
                .onFailure().invoke(e -> LOG.errorf(e, "❌ Failed to process audit event from %s", topic))
                .replaceWithVoid();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Failed to deserialize audit event from %s", topic);
            return Uni.createFrom().voidItem();
        }
    }
    
    private <T> Uni<Void> processLog(String message, String topic, Class<T> clazz, 
                                      java.util.function.Function<T, Uni<Void>> processor) {
        try {
            LOG.debugf("📩 Received log from %s", topic);
            T log = objectMapper.readValue(message, clazz);
            return processor.apply(log)
                .onItem().invoke(() -> LOG.debugf("✅ Processed log from %s", topic))
                .onFailure().invoke(e -> LOG.errorf(e, "❌ Failed to process log from %s", topic))
                .replaceWithVoid();
        } catch (Exception e) {
            LOG.errorf(e, "❌ Failed to deserialize log from %s", topic);
            return Uni.createFrom().voidItem();
        }
    }
}
