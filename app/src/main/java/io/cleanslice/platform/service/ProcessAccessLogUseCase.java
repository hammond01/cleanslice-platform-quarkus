package io.cleanslice.platform.service;
import io.cleanslice.platform.port.AccessLogRepository;

import io.cleanslice.platform.mapper.AccessLogMapper;
import io.cleanslice.platform.domain.AccessLog;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessAccessLogUseCase {

    @Inject
    AccessLogMapper mapper;

    @Inject
    AccessLogRepository repository;

    @WithTransaction
    public Uni<Void> process(io.cleanslice.platform.dto.AccessLogDto logDto) {
        AccessLog log = mapper.toEntity(logDto);
        return repository.save(log)
            .onItem().invoke(() -> Log.debugf("🌐 Saved access log: %s %s - %d", 
                log.httpMethod, log.endpoint, log.statusCode))
            .onFailure().invoke(e -> Log.errorf(e, "❌ Failed to save access log"))
            .replaceWithVoid();
    }
}


