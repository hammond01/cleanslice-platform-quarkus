package io.cleanslice.platform.infrastructure.adapter;

import io.cleanslice.platform.application.port.in.messaging.AuditEventConsumerPort;
import io.cleanslice.platform.dto.AuditEvent;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InProcessAuditEventPublisherAdapterTest {

    @Mock
    AuditEventConsumerPort auditEventConsumerPort;

    @InjectMocks
    InProcessAuditEventPublisherAdapter publisherAdapter;

    @Test
    void publishCrudEvent_whenPayloadIsValid_shouldForwardToConsumer() {
        AuditEvent event = new AuditEvent();
        event.action = "CREATE";
        event.entityType = "Product";
        when(auditEventConsumerPort.processAuditEvent(event)).thenReturn(Uni.createFrom().voidItem());

        publisherAdapter.publishCrudEvent(event);

        verify(auditEventConsumerPort).processAuditEvent(event);
    }

    @Test
    void publishErrorEvent_whenPayloadIsNull_shouldNotCallConsumer() {
        publisherAdapter.publishErrorEvent(null);

        verifyNoInteractions(auditEventConsumerPort);
    }
}
