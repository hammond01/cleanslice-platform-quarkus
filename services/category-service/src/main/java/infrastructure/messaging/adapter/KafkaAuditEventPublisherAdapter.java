package infrastructure.messaging.adapter;

import application.dto.AuditEvent;
import application.port.outbound.AuditEventPublisherPort;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.*;

/**
 * Kafka adapter for publishing audit events
 * Enhanced for POS pharmaceutical system with validation and error handling
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
    
    @PostConstruct
    void init() {
        // Configure ObjectMapper for LocalDateTime serialization
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void publishCrudEvent(AuditEvent event) {
        if (!validateAuditEvent(event)) {
            Log.errorf("Invalid audit event, skipping publish: action=%s, entity=%s", 
                event.action, event.entityType);
            return;
        }
        
        try {
            String json = objectMapper.writeValueAsString(event);
            String key = buildKafkaKey(event);
            
            crudEmitter.send(Message.of(json)
                .addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
                    .withKey(key)
                    .build()));
            
            Log.debugf("Successfully sent CRUD audit [%s]: action=%s, entity=%s, rowId=%s, user=%s, terminal=%s",
                event.correlationId, event.action, event.entityType, event.rowId, 
                event.username, event.terminalId);
            
        } catch (Exception e) {
            Log.errorf(e, "Failed to serialize/publish CRUD audit event for %s %s", 
                event.entityType, event.rowId);
        }
    }

    @Override
    public void publishErrorEvent(AuditEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            String key = event.correlationId != null ? event.correlationId : "error-" + System.currentTimeMillis();
            
            errorEmitter.send(Message.of(json)
                .addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
                    .withKey(key)
                    .build()));
            
            Log.infof("Successfully sent ERROR audit [%s]: action=%s, error=%s, terminal=%s",
                event.correlationId, event.action, event.errorMessage, event.terminalId);
            
        } catch (Exception e) {
            Log.errorf(e, "Critical: Failed to serialize/publish ERROR audit event");
        }
    }
    
    /**
     * Validate audit event before publishing
     * Ensures critical fields are present for compliance
     */
    private boolean validateAuditEvent(AuditEvent event) {
        if (event == null) {
            Log.error("Audit event is null");
            return false;
        }
        
        if (event.action == null || event.action.isBlank()) {
            Log.errorf("Audit event missing action: entity=%s", event.entityType);
            return false;
        }
        
        if (event.entityType == null || event.entityType.isBlank()) {
            Log.error("Audit event missing entityType");
            return false;
        }
        
        if (event.serviceName == null || event.serviceName.isBlank()) {
            Log.errorf("Audit event missing serviceName: entity=%s", event.entityType);
            return false;
        }
        
        if (event.correlationId == null || event.correlationId.isBlank()) {
            Log.warnf("Audit event missing correlationId: %s %s", event.entityType, event.rowId);
        }
        
        if (event.timestamp == null) {
            Log.warnf("Audit event missing timestamp: %s %s", event.entityType, event.rowId);
        }
        
        return true;
    }
    
    /**
     * Build Kafka key for partitioning
     * Uses correlationId, or falls back to rowId or timestamp
     */
    private String buildKafkaKey(AuditEvent event) {
        if (event.correlationId != null && !event.correlationId.isBlank()) {
            return event.correlationId;
        }
        if (event.rowId != null) {
            return event.entityType + "-" + event.rowId;
        }
        if (event.transactionId != null) {
            return "txn-" + event.transactionId;
        }
        return event.entityType + "-" + System.currentTimeMillis();
    }
}
