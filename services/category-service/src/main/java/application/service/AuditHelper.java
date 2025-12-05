package application.service;

import application.dto.AuditEvent;
import application.port.outbound.AuditEventPublisherPort;
import domain.enums.AuditType;
import infrastructure.persistence.UserContext;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import share.context.PosContext;
import share.enums.AuditStatusEnum;

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

    @Inject
    PosContext posContext;

    /**
     * Create a base audit event with common fields populated
     */
    public AuditEvent.Builder createBaseEvent(String serviceName, AuditType auditType, String action) {
        AuditEvent.Builder builder = AuditEvent.builder()
                .auditType(auditType)
                .action(action)
                .serviceName(serviceName)
                .correlationId(UUID.randomUUID().toString())
                .status(AuditStatusEnum.SUCCESS);

        // Add user context
        try {
            builder.username(userContext.getUsername())
                   .ipAddress(userContext.getIpAddress());
            
            if (userContext.getCurrentUserId() != null && !"system".equals(userContext.getCurrentUserId())) {
                builder.userId(Long.parseLong(userContext.getCurrentUserId()));
            }
        } catch (Exception ex) {
            Log.warnf("Failed to extract user context for audit: %s", ex.getMessage());
        }

        // Add POS context
        try {
            if (posContext.getTerminalId() != null) {
                builder.terminalId(posContext.getTerminalId());
            }
            if (posContext.getStoreId() != null) {
                builder.storeId(posContext.getStoreId());
            }
            if (posContext.getShiftId() != null) {
                builder.shiftId(posContext.getShiftId());
            }
            if (posContext.getPharmacistId() != null) {
                builder.pharmacistId(posContext.getPharmacistId());
            }
        } catch (Exception ex) {
            Log.warnf("Failed to extract POS context for audit: %s", ex.getMessage());
        }

        return builder;
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
            if (event.auditType == AuditType.ERROR) {
                publishErrorEvent(event);
            } else {
                publishCrudEvent(event);
            }
        } catch (Exception ex) {
            Log.errorf(ex, "Failed to publish audit event: %s", ex.getMessage());
        }
    }
}
