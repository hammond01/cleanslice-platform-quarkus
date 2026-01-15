package application.port.outbound;

import share.dto.AuditEvent;

/**
 * Port for publishing audit events (Outbound port)
 * Infrastructure will implement this interface
 */
public interface AuditEventPublisherPort {
    
    /**
     * Publish CRUD audit event
     */
    void publishCrudEvent(AuditEvent event);
    
    /**
     * Publish error audit event
     */
    void publishErrorEvent(AuditEvent event);
}
