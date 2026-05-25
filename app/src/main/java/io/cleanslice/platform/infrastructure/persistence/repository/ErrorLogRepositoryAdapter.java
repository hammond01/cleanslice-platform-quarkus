package io.cleanslice.platform.infrastructure.persistence.repository;

import io.cleanslice.platform.port.ErrorLogRepository;
import io.cleanslice.platform.domain.ErrorLog;
import io.cleanslice.platform.infrastructure.persistence.entity.ErrorLogEntity;
import io.cleanslice.platform.infrastructure.persistence.mapper.ErrorLogEntityMapper;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ErrorLogRepositoryAdapter implements ErrorLogRepository {

    @Inject
    ErrorLogEntityMapper mapper;

    @Override
    @WithSession
    public Uni<ErrorLog> save(ErrorLog log) {
        ErrorLogEntity entity = mapper.toEntity(log);
        return ErrorLogEntity.getSession().flatMap(session -> session.merge(entity))
                .replaceWith(() -> mapper.toDomain(entity));
    }
}
