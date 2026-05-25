package io.cleanslice.platform.port;

import io.cleanslice.platform.domain.PerformanceLog;
import io.smallrye.mutiny.Uni;

public interface PerformanceLogRepository {
    Uni<PerformanceLog> save(PerformanceLog log);
}
