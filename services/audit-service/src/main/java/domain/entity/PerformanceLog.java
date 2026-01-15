package domain.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_logs", indexes = {
        @Index(name = "idx_perf_service", columnList = "service_name"),
        @Index(name = "idx_perf_operation", columnList = "operation"),
        @Index(name = "idx_perf_type", columnList = "operation_type"),
        @Index(name = "idx_perf_timestamp", columnList = "timestamp"),
        @Index(name = "idx_perf_slow", columnList = "is_slow"),
        @Index(name = "idx_perf_duration", columnList = "duration_ms")
})
public class PerformanceLog extends PanacheEntity {

    @Column(name = "service_name", nullable = false, length = 100)
    public String serviceName;

    @Column(name = "operation", nullable = false, length = 255)
    public String operation;

    @Column(name = "operation_type", length = 50)
    public String operationType;

    @Column(name = "duration_ms")
    public Long durationMs;

    @Column(name = "threshold_ms")
    public Long thresholdMs;

    @Column(name = "is_slow")
    public Boolean isSlow = false;

    @Column(name = "memory_used_mb")
    public Long memoryUsedMb;

    @Column(name = "cpu_percent")
    public Double cpuPercent;

    @Column(name = "thread_count")
    public Integer threadCount;

    @Column(name = "sql_query", columnDefinition = "TEXT")
    public String sqlQuery;

    @Column(name = "query_time_ms")
    public Long queryTimeMs;

    @Column(name = "rows_affected")
    public Integer rowsAffected;

    @Column(name = "connection_pool_size")
    public Integer connectionPoolSize;

    @Column(name = "http_method", length = 10)
    public String httpMethod;

    @Column(name = "endpoint", length = 500)
    public String endpoint;

    @Column(name = "status_code")
    public Integer statusCode;

    @Column(name = "user_id", length = 100)
    public String userId;

    @Column(name = "correlation_id", length = 100)
    public String correlationId;

    @Column(name = "transaction_id", length = 100)
    public String transactionId;

    @Column(name = "metadata", columnDefinition = "TEXT")
    public String metadata;

    @Column(name = "timestamp", nullable = false)
    public LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "terminal_id", length = 50)
    public String terminalId;

    @Column(name = "store_id", length = 50)
    public String storeId;
}
