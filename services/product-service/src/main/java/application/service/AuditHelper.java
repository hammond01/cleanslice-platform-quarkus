package application.service;

import share.dto.AuditEvent;
import application.port.outbound.AuditEventPublisherPort;
import share.enums.AuditTypeEnum;
import infrastructure.persistence.UserContext;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import share.enums.AuditStatusEnum;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Helper service for creating and publishing audit events
 * Provides convenient methods for common audit scenarios in POS system
 */
@ApplicationScoped
public class AuditHelper {

    @Inject
    AuditEventPublisherPort auditEventPublisher;

    @Inject
    UserContext userContext;

    /**
     * Create a base audit event with common fields populated
     */
    public AuditEvent createBaseEvent(String serviceName, AuditTypeEnum auditType, String action) {
        AuditEvent event = new AuditEvent();
        event.auditTypeEnum = auditType;
        event.action = action;
        event.serviceName = serviceName;
        event.timestamp = LocalDateTime.now();
        event.correlationId = UUID.randomUUID().toString();
        event.status = AuditStatusEnum.SUCCESS;

        // Add user context
        try {
            event.username = userContext.getUsername();
            event.ipAddress = userContext.getIpAddress();
            
            if (userContext.getCurrentUserId() != null && !"system".equals(userContext.getCurrentUserId())) {
                event.userId = Long.parseLong(userContext.getCurrentUserId());
            }
        } catch (Exception ex) {
            Log.warnf("Failed to extract user context for audit: %s", ex.getMessage());
        }

        return event;
    }

    /**
     * Publish CRUD event with error handling
     */
    public void publishCrudEvent(AuditEvent event) {
        try {
            auditEventPublisher.publishCrudEvent(event);
            Log.debugf("Published audit event [%s] for %s: %s %s", 
                event.correlationId, event.action, event.entityType, event.rowId);
        } catch (Exception ex) {
            Log.errorf(ex, "Critical: Failed to publish audit event for %s on %s %s", 
                event.action, event.entityType, event.rowId);
        }
    }

    /**
     * Publish error event with error handling
     */
    public void publishErrorEvent(AuditEvent event) {
        try {
            auditEventPublisher.publishErrorEvent(event);
            Log.debugf("Published error audit event [%s]: %s", 
                event.correlationId, event.errorMessage);
        } catch (Exception ex) {
            Log.errorf(ex, "Critical: Failed to publish error audit event");
        }
    }

    /**
     * Safely publish audit event without throwing exceptions
     */
    public void safePublish(AuditEvent event) {
        try {
            if (event.auditTypeEnum == AuditTypeEnum.ERROR) {
                publishErrorEvent(event);
            } else {
                publishCrudEvent(event);
            }
        } catch (Exception ex) {
            Log.errorf(ex, "Failed to publish audit event: %s", ex.getMessage());
        }
    }
}
