package domain.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;
import share.enums.LogLevel;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_logs", indexes = {
        @Index(name = "idx_app_level", columnList = "level"),
        @Index(name = "idx_app_service", columnList = "service_name"),
        @Index(name = "idx_app_timestamp", columnList = "timestamp"),
        @Index(name = "idx_app_correlation", columnList = "correlation_id"),
        @Index(name = "idx_app_user", columnList = "user_id")
})
public class ApplicationLog extends PanacheEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 20)
    public LogLevel level;

    @Column(name = "service_name", nullable = false, length = 100)
    public String serviceName;

    @Column(name = "logger", length = 255)
    public String logger;

    @Column(name = "message", columnDefinition = "TEXT")
    public String message;

    @Column(name = "thread", length = 100)
    public String thread;

    @Column(name = "method", length = 255)
    public String method;

    @Column(name = "class_name", length = 255)
    public String className;

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

    @Column(name = "file_name", length = 255)
    public String fileName;

    @Column(name = "line_number")
    public Integer lineNumber;

    @Column(name = "metadata", columnDefinition = "TEXT")
    public String metadata;

    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "terminal_id", length = 50)
    public String terminalId;

    @Column(name = "store_id", length = 50)
    public String storeId;

    @Column(name = "shift_id", length = 50)
    public String shiftId;
}
