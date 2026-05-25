package io.cleanslice.platform.port;

import io.cleanslice.platform.domain.ErrorLog;
import io.smallrye.mutiny.Uni;

public interface ErrorLogRepository {
    Uni<ErrorLog> save(ErrorLog log);
}
