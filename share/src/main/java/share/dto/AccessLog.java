package share.dto;

import java.time.LocalDateTime;

/**
 * DTO for HTTP access logs
 * Tracks API requests, response times, status codes
 */
public class AccessLog {
    public String serviceName;
    public String httpMethod;
    public String endpoint;
    public String path;
    public String queryString;
    
    // Request
    public String requestId;
    public String ipAddress;
    public String userAgent;
    public String referer;
    public String origin;
    public Integer requestSize;     // bytes
    
    // Response
    public Integer statusCode;
    public Integer responseSize;    // bytes
    public String contentType;
    
    // Timing
    public Long responseTimeMs;
    public LocalDateTime requestTime;
    public LocalDateTime responseTime;
    
    // User context
    public String userId;
    public String username;
    public String sessionId;
    public String correlationId;
    
    // Security
    public String authMethod;       // "Bearer", "Basic", "Session"
    public Boolean authenticated = false;
    
    // Additional data
    public String metadata;         // JSON for headers, etc.
    public LocalDateTime timestamp = LocalDateTime.now();
    
    // POS context
    public String terminalId;
    public String storeId;
}
