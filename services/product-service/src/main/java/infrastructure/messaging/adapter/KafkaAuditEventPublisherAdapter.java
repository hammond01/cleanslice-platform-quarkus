package infrastructure.messaging.adapter;

import application.dto.AuditEvent;
import application.port.outbound.AuditEventPublisherPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

/**
 * Kafka adapter for publishing audit events
 * Implements the outbound port
 */
@ApplicationScoped
public class KafkaAuditEventPublisherAdapter implements AuditEventPublisherPort {

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
            String key = event.correlationId != null ? event.correlationId : event.entityId;
            
            crudEmitter.send(Message.of(json)
                .addMetadata(OutgoingKafkaRecordMetadata.builder()
                    .withKey(key)
                    .build()));
            
            Log.infof("Published CRUD audit: action=%s, entity=%s, id=%s, correlation=%s", 
                event.action, event.entityType, event.entityId, event.correlationId);
        } catch (Exception e) {
            Log.errorf(e, "Failed to publish CRUD audit event for %s %s", event.entityType, event.entityId);
        }
    }

    @Override
    public void publishErrorEvent(AuditEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            String key = event.correlationId != null ? event.correlationId : "error";
            
            errorEmitter.send(Message.of(json)
                .addMetadata(OutgoingKafkaRecordMetadata.builder()
                    .withKey(key)
                    .build()));
            
            Log.infof("Published ERROR audit: action=%s, correlation=%s", event.action, event.correlationId);
        } catch (Exception e) {
            Log.errorf(e, "Failed to publish ERROR audit event");
        }
    }
}
