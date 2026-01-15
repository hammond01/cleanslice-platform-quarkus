package share.dto;

import java.time.LocalDateTime;

/**
 * DTO for performance monitoring logs
 * Tracks slow operations, metrics, resource usage
 */
public class PerformanceLog {
    public String serviceName;
    public String operation;        // e.g., "createProduct", "queryCategories"
    public String operationType;    // "DATABASE", "HTTP", "KAFKA", "BUSINESS_LOGIC"
    
    // Timing
    public Long durationMs;
    public Long thresholdMs;        // Expected max duration
    public Boolean isSlow = false;
    
    // Resource usage
    public Long memoryUsedMb;
    public Double cpuPercent;
    public Integer threadCount;
    
    // Database metrics (if applicable)
    public String sqlQuery;
    public Long queryTimeMs;
    public Integer rowsAffected;
    public Integer connectionPoolSize;
    
    // HTTP metrics (if applicable)
    public String httpMethod;
    public String endpoint;
    public Integer statusCode;
    
    // Context
    public String userId;
    public String correlationId;
    public String transactionId;
    
    // Additional data
    public String metadata;         // JSON for detailed metrics
    public LocalDateTime timestamp = LocalDateTime.now();
    
    // POS context
    public String terminalId;
    public String storeId;
}
