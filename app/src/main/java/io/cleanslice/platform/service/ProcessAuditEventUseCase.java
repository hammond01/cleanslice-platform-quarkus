package io.cleanslice.platform.service;

import io.cleanslice.platform.port.AuditLogRepository;
import io.cleanslice.platform.dto.AuditEvent;
import io.cleanslice.platform.port.AuditEventConsumerPort;
import io.cleanslice.platform.mapper.AuditMapper;
import io.cleanslice.platform.domain.AuditLog;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProcessAuditEventUseCase implements AuditEventConsumerPort {

    private static final Logger LOG = Logger.getLogger(ProcessAuditEventUseCase.class);

    @Inject
    AuditMapper auditMapper;

    @Inject
    AuditLogRepository repository;

    @Override
    @WithTransaction
    public Uni<Void> processAuditEvent(AuditEvent event) {
        LOG.infof("Processing audit event: Type=%s, Action=%s", event.auditTypeEnum, event.action);

        AuditLog auditLog = auditMapper.toEntity(event);
        return repository.save(auditLog)
            .onItem().invoke(persisted -> {
                LOG.infof("Audit log persisted: ID=%d, Type=%s, Action=%s",
                    auditLog.id, auditLog.auditType, auditLog.action);
            })
            .replaceWithVoid();
    }
}
