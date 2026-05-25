package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.port.PerformanceLogRepository;
import io.cleanslice.platform.domain.PerformanceLog;
import io.cleanslice.platform.infrastructure.persistence.entity.PerformanceLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.PerformanceLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PerformanceLogRepositoryAdapter implements PerformanceLogRepository {

    @Inject
    PerformanceLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<PerformanceLog> save(PerformanceLog log) {
        PerformanceLogEntity entity = mapper.toEntity(log);
        return PerformanceLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }
}
