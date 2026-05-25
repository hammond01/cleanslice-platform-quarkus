package io.cleanslice.platform.port;

import io.cleanslice.platform.domain.ApplicationLog;
import io.smallrye.mutiny.Uni;

public interface ApplicationLogRepository {
    Uni<ApplicationLog> save(ApplicationLog log);
}
