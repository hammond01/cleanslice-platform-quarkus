package domain.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import share.enums.LogLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_logs", indexes = {
        @Index(name = "idx_error_level", columnList = "level"),
        @Index(name = "idx_error_service", columnList = "service_name"),
        @Index(name = "idx_error_type", columnList = "exception_type"),
        @Index(name = "idx_error_timestamp", columnList = "timestamp"),
        @Index(name = "idx_error_correlation", columnList = "correlation_id"),
        @Index(name = "idx_error_category", columnList = "category"),
        @Index(name = "idx_error_resolved", columnList = "resolved")
})
public class ErrorLog extends PanacheEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    public LogLevel level = LogLevel.ERROR;

    @Column(name = "service_name", nullable = false, length = 100)
    public String serviceName;

    @Column(name = "exception_type", length = 255)
    public String exceptionType;

    @Column(name = "message", columnDefinition = "TEXT")
    public String message;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    public String stackTrace;

    @Column(name = "root_cause", columnDefinition = "TEXT")
    public String rootCause;

    @Column(name = "user_id", length = 100)
    public String userId;

    @Column(name = "username", length = 255)
    public String username;

    @Column(name = "session_id", length = 100)
    public String sessionId;

    @Column(name = "correlation_id", length = 100)
    public String correlationId;

    @Column(name = "transaction_id", length = 100)
    public String transactionId;

    @Column(name = "class_name", length = 255)
    public String className;

    @Column(name = "method", length = 255)
    public String method;

    @Column(name = "file_name", length = 255)
    public String fileName;

    @Column(name = "line_number")
    public Integer lineNumber;

    @Column(name = "http_method", length = 10)
    public String httpMethod;

    @Column(name = "endpoint", length = 500)
    public String endpoint;

    @Column(name = "ip_address", length = 50)
    public String ipAddress;

    @Column(name = "user_agent", length = 500)
    public String userAgent;

    @Column(name = "metadata", columnDefinition = "TEXT")
    public String metadata;

    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "error_code", length = 50)
    public String errorCode;

    @Column(name = "category", length = 100)
    public String category;

    @Column(name = "resolved")
    public Boolean resolved = false;

    @Column(name = "resolution", columnDefinition = "TEXT")
    public String resolution;

    @Column(name = "terminal_id", length = 50)
    public String terminalId;

    @Column(name = "store_id", length = 50)
    public String storeId;
}
