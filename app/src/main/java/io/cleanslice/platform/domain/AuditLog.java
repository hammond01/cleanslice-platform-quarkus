package io.cleanslice.platform.domain;

import io.cleanslice.platform.domain.enums.AuditStatusEnum;
import io.cleanslice.platform.domain.enums.AuditTypeEnum;
import io.cleanslice.platform.domain.enums.Severity;

import java.time.LocalDateTime;

public class AuditLog {

    public Long id;
    public AuditTypeEnum auditType;
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
    public String metadata;
    public AuditStatusEnum status = AuditStatusEnum.SUCCESS;
    public String errorMessage;
    public String stackTrace;
    public Severity severity;
    public LocalDateTime timestamp = LocalDateTime.now();
    public Long durationMs;
    public String correlationId;
    public String sessionId;
}



