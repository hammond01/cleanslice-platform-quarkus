package infrastructure.messaging.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import share.dto.AuditEvent;
import application.port.inbound.AuditEventConsumerPort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Kafka adapter for consuming audit events
 * Implements the inbound port by delegating to use case
 */
@ApplicationScoped
public class KafkaAuditEventConsumerAdapter {

    private static final Logger LOG = Logger.getLogger(KafkaAuditEventConsumerAdapter.class);

    @Inject
    ObjectMapper objectMapper;
    
    @Inject
    AuditEventConsumerPort processAuditEventUseCase;

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
}
