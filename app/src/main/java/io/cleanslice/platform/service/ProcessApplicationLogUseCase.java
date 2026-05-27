package io.cleanslice.platform.service;
import io.cleanslice.platform.application.port.out.persistence.ApplicationLogRepository;

import io.cleanslice.platform.mapper.ApplicationLogMapper;
import io.cleanslice.platform.domain.ApplicationLog;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessApplicationLogUseCase {

    @Inject
    ApplicationLogMapper mapper;

    @Inject
    ApplicationLogRepository repository;

    @WithTransaction
    public Uni<Void> process(io.cleanslice.platform.dto.ApplicationLogDto logDto) {
        ApplicationLog log = mapper.toEntity(logDto);
        return repository.save(log)
            .onItem().invoke(() -> Log.debugf("✅ Saved application log: %s - %s", 
                log.level, log.message))
            .onFailure().invoke(e -> Log.errorf(e, "❌ Failed to save application log"))
            .replaceWithVoid();
    }
}


