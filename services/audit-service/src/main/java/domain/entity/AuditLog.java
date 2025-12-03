package domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import share.enums.AuditStatusEnum;
import share.enums.Severity;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_type", columnList = "audit_type"),
        @Index(name = "idx_entity_type", columnList = "entity_type"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_timestamp", columnList = "timestamp"),
        @Index(name = "idx_service_name", columnList = "service_name")
})
public class AuditLog extends PanacheEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "audit_type", nullable = false, length = 50)
    public AuditType auditType;

    @Column(name = "action", nullable = false, length = 100)
    public String action;

    @Column(name = "entity_type", length = 100)
    public String entityType;

    @Column(name = "entity_id")
    public Long entityId;

    @Column(name = "user_id")
    public Long userId;

    @Column(name = "username", length = 255)
    public String username;

    @Column(name = "service_name", nullable = false, length = 100)
    public String serviceName;

    @Column(name = "ip_address", length = 50)
    public String ipAddress;

    @Column(name = "user_agent", length = 500)
    public String userAgent;

    @Column(name = "http_method", length = 10)
    public String httpMethod;

    @Column(name = "endpoint", length = 500)
    public String endpoint;

    @Column(name = "old_value", columnDefinition = "TEXT")
    public String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    public String newValue;

    @Column(name = "metadata", columnDefinition = "TEXT")
    public String metadata;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    public AuditStatusEnum status = AuditStatusEnum.SUCCESS;

    @Column(name = "error_message", columnDefinition = "TEXT")
    public String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    public String stackTrace;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 20)
    public Severity severity;

    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "duration_ms")
    public Long durationMs;

    @Column(name = "correlation_id", length = 100)
    public String correlationId;

    @Column(name = "session_id", length = 100)
    public String sessionId;
}
