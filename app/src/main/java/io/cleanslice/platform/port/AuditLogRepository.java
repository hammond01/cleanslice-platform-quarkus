package io.cleanslice.platform.port;

import io.cleanslice.platform.domain.AuditLog;
import io.smallrye.mutiny.Uni;

public interface AuditLogRepository {
    Uni<AuditLog> save(AuditLog log);
}
