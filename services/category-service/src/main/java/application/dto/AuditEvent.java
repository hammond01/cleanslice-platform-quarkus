package application.dto;

import domain.enums.AuditType;
import domain.enums.Severity;
import share.enums.AuditStatusEnum;

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
    public String metadata;
    public AuditStatusEnum status = AuditStatusEnum.SUCCESS;
    public String errorMessage;
    public String stackTrace;
    public Severity severity;
    public LocalDateTime timestamp = LocalDateTime.now();
    public Long durationMs;
    public String correlationId;
    public String sessionId;

    public static class Builder {
        private final AuditEvent event = new AuditEvent();

        public Builder auditType(AuditType auditType) {
            event.auditType = auditType;
            return this;
        }

        public Builder action(String action) {
            event.action = action;
            return this;
        }

        public Builder entityType(String entityType) {
            event.entityType = entityType;
            return this;
        }

        public Builder entityId(Long entityId) {
            event.entityId = entityId;
            return this;
        }

        public Builder serviceName(String serviceName) {
            event.serviceName = serviceName;
            return this;
        }

        public Builder oldValue(String oldValue) {
            event.oldValue = oldValue;
            return this;
        }

        public Builder newValue(String newValue) {
            event.newValue = newValue;
            return this;
        }

        public Builder status(AuditStatusEnum status) {
            event.status = status;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            event.errorMessage = errorMessage;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            event.stackTrace = stackTrace;
            return this;
        }

        public Builder severity(Severity severity) {
            event.severity = severity;
            return this;
        }

        public Builder correlationId(String correlationId) {
            event.correlationId = correlationId;
            return this;
        }

        public AuditEvent build() {
            return event;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
