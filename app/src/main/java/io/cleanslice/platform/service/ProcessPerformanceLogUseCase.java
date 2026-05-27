package io.cleanslice.platform.service;
import io.cleanslice.platform.application.port.out.persistence.PerformanceLogRepository;

import io.cleanslice.platform.mapper.PerformanceLogMapper;
import io.cleanslice.platform.domain.PerformanceLog;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessPerformanceLogUseCase {

    @Inject
    PerformanceLogMapper mapper;

    @Inject
    PerformanceLogRepository repository;

    @WithTransaction
    public Uni<Void> process(io.cleanslice.platform.dto.PerformanceLogDto logDto) {
        PerformanceLog log = mapper.toEntity(logDto);
        return repository.save(log)
            .onItem().invoke(() -> {
                String emoji = Boolean.TRUE.equals(log.isSlow) ? "🐌" : "⚡";
                Log.debugf("%s Saved performance log: %s - %dms", 
                    emoji, log.operation, log.durationMs);
            })
            .onFailure().invoke(e -> Log.errorf(e, "❌ Failed to save performance log"))
            .replaceWithVoid();
    }
}


