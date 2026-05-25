package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.port.ApplicationLogRepository;
import io.cleanslice.platform.domain.ApplicationLog;
import io.cleanslice.platform.infrastructure.persistence.entity.ApplicationLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.ApplicationLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ApplicationLogRepositoryAdapter implements ApplicationLogRepository {

    @Inject
    ApplicationLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<ApplicationLog> save(ApplicationLog log) {
        ApplicationLogEntity entity = mapper.toEntity(log);
        return ApplicationLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }
}
