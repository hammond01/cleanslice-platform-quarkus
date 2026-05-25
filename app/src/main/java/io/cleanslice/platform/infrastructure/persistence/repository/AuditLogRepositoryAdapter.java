package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.port.AuditLogRepository;
import io.cleanslice.platform.domain.AuditLog;
import io.cleanslice.platform.infrastructure.persistence.entity.AuditLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.AuditLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    @Inject
    AuditLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<AuditLog> save(AuditLog log) {
        AuditLogEntity entity = mapper.toEntity(log);
        return AuditLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }
}
