package io.cleanslice.platform;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TemplateSmokeTest {

    @Test
    void templatePortsAreDiscoverable() {
        assertDoesNotThrow(() -> Class.forName("io.cleanslice.platform.application.port.out.persistence.ProductRepository"));
        assertDoesNotThrow(() -> Class.forName("io.cleanslice.platform.application.port.out.messaging.AuditEventPublisherPort"));
        assertDoesNotThrow(() -> Class.forName("io.cleanslice.platform.application.port.in.messaging.AuditEventConsumerPort"));
    }
}
