package domain.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs", indexes = {
        @Index(name = "idx_access_service", columnList = "service_name"),
        @Index(name = "idx_access_method", columnList = "http_method"),
        @Index(name = "idx_access_status", columnList = "status_code"),
        @Index(name = "idx_access_timestamp", columnList = "timestamp"),
        @Index(name = "idx_access_user", columnList = "user_id"),
        @Index(name = "idx_access_ip", columnList = "ip_address"),
        @Index(name = "idx_access_endpoint", columnList = "endpoint")
})
public class AccessLog extends PanacheEntity {

    @Column(name = "service_name", nullable = false, length = 100)
    public String serviceName;

    @Column(name = "http_method", nullable = false, length = 10)
    public String httpMethod;

    @Column(name = "endpoint", length = 500)
    public String endpoint;

    @Column(name = "path", length = 500)
    public String path;

    @Column(name = "query_string", length = 1000)
    public String queryString;

    @Column(name = "request_id", length = 100)
    public String requestId;

    @Column(name = "ip_address", length = 50)
    public String ipAddress;

    @Column(name = "user_agent", length = 500)
    public String userAgent;

    @Column(name = "referer", length = 500)
    public String referer;

    @Column(name = "origin", length = 255)
    public String origin;

    @Column(name = "request_size")
    public Integer requestSize;

    @Column(name = "status_code")
    public Integer statusCode;

    @Column(name = "response_size")
    public Integer responseSize;

    @Column(name = "content_type", length = 100)
    public String contentType;

    @Column(name = "response_time_ms")
    public Long responseTimeMs;

    @Column(name = "request_time")
    public LocalDateTime requestTime;

    @Column(name = "response_time")
    public LocalDateTime responseTime;

    @Column(name = "user_id", length = 100)
    public String userId;

    @Column(name = "username", length = 255)
    public String username;

    @Column(name = "session_id", length = 100)
    public String sessionId;

    @Column(name = "correlation_id", length = 100)
    public String correlationId;

    @Column(name = "auth_method", length = 50)
    public String authMethod;

    @Column(name = "authenticated")
    public Boolean authenticated = false;

    @Column(name = "metadata", columnDefinition = "TEXT")
    public String metadata;

    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "terminal_id", length = 50)
    public String terminalId;

    @Column(name = "store_id", length = 50)
    public String storeId;
}
