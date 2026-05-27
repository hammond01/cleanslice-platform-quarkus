package io.cleanslice.platform.application.port.out.messaging;

import io.cleanslice.platform.dto.AuditEvent;

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


