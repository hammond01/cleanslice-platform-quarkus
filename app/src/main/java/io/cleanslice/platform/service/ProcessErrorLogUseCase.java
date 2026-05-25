package io.cleanslice.platform.service;
import io.cleanslice.platform.port.ErrorLogRepository;

import io.cleanslice.platform.mapper.ErrorLogMapper;
import io.cleanslice.platform.domain.ErrorLog;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessErrorLogUseCase {

    @Inject
    ErrorLogMapper mapper;

    @Inject
    ErrorLogRepository repository;

    @WithTransaction
    public Uni<Void> process(io.cleanslice.platform.dto.ErrorLogDto dto) {
        ErrorLog log = mapper.toEntity(dto);
        return repository.save(log)
            .onItem().invoke(() -> Log.warnf("⚠️ Saved error log: %s - %s",
                log.exceptionType, log.message))
            .onFailure().invoke(e -> Log.errorf(e, "❌ Failed to save error log"))
            .replaceWithVoid();
    }
}



