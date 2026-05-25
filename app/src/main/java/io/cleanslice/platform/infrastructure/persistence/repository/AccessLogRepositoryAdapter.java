package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.port.AccessLogRepository;
import io.cleanslice.platform.domain.AccessLog;
import io.cleanslice.platform.infrastructure.persistence.entity.AccessLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.AccessLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccessLogRepositoryAdapter implements AccessLogRepository {

    @Inject
    AccessLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<AccessLog> save(AccessLog log) {
        AccessLogEntity entity = mapper.toEntity(log);
        return AccessLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }
}
