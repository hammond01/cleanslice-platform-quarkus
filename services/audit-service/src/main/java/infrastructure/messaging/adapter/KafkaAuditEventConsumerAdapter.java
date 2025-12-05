package infrastructure.messaging.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import application.dto.AuditEvent;
import application.port.inbound.AuditEventConsumerPort;
import io.smallrye.reactive.messaging.annotations.Blocking;
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
    @Blocking
    public void consumeLoginEvent(String message) {
        processEvent(message, "login-events");
    }

    @Incoming("crud-events")
    @Blocking
    public void consumeCrudEvent(String message) {
        processEvent(message, "crud-events");
    }

    @Incoming("transaction-events")
    @Blocking
    public void consumeTransactionEvent(String message) {
        processEvent(message, "transaction-events");
    }

    @Incoming("security-events")
    @Blocking
    public void consumeSecurityEvent(String message) {
        processEvent(message, "security-events");
    }

    @Incoming("system-events")
    @Blocking
    public void consumeSystemEvent(String message) {
        processEvent(message, "system-events");
    }

    @Incoming("error-events")
    @Blocking
    public void consumeErrorEvent(String message) {
        processEvent(message, "error-events");
    }

    private void processEvent(String message, String topic) {
        try {
            LOG.infof("Received audit event from %s: %s", topic, message);
            AuditEvent event = objectMapper.readValue(message, AuditEvent.class);
            processAuditEventUseCase.processAuditEvent(event)
                .await().indefinitely();
        } catch (Exception e) {
            LOG.errorf(e, "Failed to process audit event from %s", topic);
        }
    }
}
