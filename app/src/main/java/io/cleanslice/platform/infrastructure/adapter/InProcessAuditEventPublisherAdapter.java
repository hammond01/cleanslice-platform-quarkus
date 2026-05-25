package io.cleanslice.platform.infrastructure.adapter;

import io.cleanslice.platform.dto.AuditEvent;
import io.cleanslice.platform.port.AuditEventConsumerPort;
import io.cleanslice.platform.port.AuditEventPublisherPort;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InProcessAuditEventPublisherAdapter implements AuditEventPublisherPort {

    @Inject
    AuditEventConsumerPort auditEventConsumerPort;

    @Override
    public void publishCrudEvent(AuditEvent event) {
        publish(event, "CRUD");
    }

    @Override
    public void publishErrorEvent(AuditEvent event) {
        publish(event, "ERROR");
    }

    private void publish(AuditEvent event, String eventType) {
        if (event == null) {
            Log.errorf("Cannot publish %s audit event: payload is null", eventType);
            return;
        }

        auditEventConsumerPort.processAuditEvent(event)
                .subscribe().with(
                        unused -> Log.debugf(
                                "Persisted %s audit event in-process: action=%s, entity=%s, rowId=%s",
                                eventType, event.action, event.entityType, event.rowId),
                        failure -> Log.errorf(
                                failure,
                                "Failed to persist %s audit event in-process: action=%s, entity=%s, rowId=%s",
                                eventType, event.action, event.entityType, event.rowId));
    }
}
