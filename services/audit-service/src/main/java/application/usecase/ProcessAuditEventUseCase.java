package application.usecase;

import application.dto.AuditEvent;
import application.port.inbound.AuditEventConsumerPort;
import application.mapper.AuditMapper;
import domain.entity.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Use case for processing audit events
 * This is the application service that handles business logic
 */
@ApplicationScoped
public class ProcessAuditEventUseCase implements AuditEventConsumerPort {

    private static final Logger LOG = Logger.getLogger(ProcessAuditEventUseCase.class);
    
    @Inject
    AuditMapper auditMapper;

    @Override
    @Transactional
    public void processAuditEvent(AuditEvent event) {
        LOG.infof("Processing audit event: Type=%s, Action=%s", event.auditType, event.action);
        
        AuditLog auditLog = auditMapper.toEntity(event);
        auditLog.persist();
        
        LOG.infof("Audit log persisted: ID=%d, Type=%s, Action=%s", 
                auditLog.id, auditLog.auditType, auditLog.action);
    }
}
