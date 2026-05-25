package io.cleanslice.platform.port;

import io.cleanslice.platform.dto.AuditEvent;
import io.smallrye.mutiny.Uni;

/**
 * Port for consuming audit events (Inbound port)
 * Infrastructure will implement this interface
 */
public interface AuditEventConsumerPort {
    
    /**
     * Process incoming audit event
     */
    Uni<Void> processAuditEvent(AuditEvent event);
}


