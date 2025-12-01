package application.port.inbound;

import application.dto.AuditEvent;

/**
 * Port for consuming audit events (Inbound port)
 * Infrastructure will implement this interface
 */
public interface AuditEventConsumerPort {
    
    /**
     * Process incoming audit event
     */
    void processAuditEvent(AuditEvent event);
}
