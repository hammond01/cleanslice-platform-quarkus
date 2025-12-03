package application.dto;

import domain.entity.AuditType;
import share.enums.AuditStatusEnum;
import share.enums.Severity;

import java.time.LocalDateTime;

public class AuditEvent {
    public AuditType auditType;
    public String action;
    public String entityType;
    public Long entityId;
    public Long userId;
    public String username;
    public String serviceName;
    public String ipAddress;
    public String userAgent;
    public String httpMethod;
    public String endpoint;
    public String oldValue;
    public String newValue;
    public AuditStatusEnum status = AuditStatusEnum.SUCCESS;
    public String metadata;
    public String errorMessage;
    public String stackTrace;
    public Severity severity;
    public LocalDateTime timestamp = LocalDateTime.now();
    public Long durationMs;
    public String correlationId;
    public String sessionId;
}
