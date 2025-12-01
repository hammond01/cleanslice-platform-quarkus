package infrastructure.messaging.adapter;

import application.dto.AuditEvent;
import application.port.outbound.AuditEventPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;

/**
 * Kafka adapter for publishing audit events
 * Implements the outbound port
 */
@ApplicationScoped
public class KafkaAuditEventPublisherAdapter implements AuditEventPublisherPort {

    private static final Logger LOG = Logger.getLogger(KafkaAuditEventPublisherAdapter.class);

    @Inject
    @Channel("audit-crud")
    Emitter<String> crudEmitter;

    @Inject
    @Channel("audit-error")
    Emitter<String> errorEmitter;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void publishCrudEvent(AuditEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            crudEmitter.send(Message.of(json)
                .addMetadata(OutgoingKafkaRecordMetadata.builder()
                    .withKey(event.correlationId)
                    .build()));
            LOG.infof("Published CRUD audit event: %s", event.action);
        } catch (Exception e) {
            LOG.errorf(e, "Failed to publish CRUD audit event");
        }
    }

    @Override
    public void publishErrorEvent(AuditEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            errorEmitter.send(Message.of(json)
                .addMetadata(OutgoingKafkaRecordMetadata.builder()
                    .withKey(event.correlationId)
                    .build()));
            LOG.infof("Published ERROR audit event: %s", event.action);
        } catch (Exception e) {
            LOG.errorf(e, "Failed to publish ERROR audit event");
        }
    }
}
