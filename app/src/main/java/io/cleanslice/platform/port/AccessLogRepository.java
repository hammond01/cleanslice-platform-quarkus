package io.cleanslice.platform.port;

import io.cleanslice.platform.domain.AccessLog;
import io.smallrye.mutiny.Uni;

public interface AccessLogRepository {
    Uni<AccessLog> save(AccessLog log);
}
