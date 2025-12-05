package share.dto;

import share.enums.LogLevel;
import java.time.LocalDateTime;

/**
 * DTO for error and exception logs
 * Centralized error tracking for debugging and monitoring
 */
public class ErrorLog {
    public LogLevel level = LogLevel.ERROR;
    public String serviceName;
    public String exceptionType;
    public String message;
    public String stackTrace;
    public String rootCause;
    
    // Context when error occurred
    public String userId;
    public String username;
    public String sessionId;
    public String correlationId;
    public String transactionId;
    
    // Location
    public String className;
    public String method;
    public String fileName;
    public Integer lineNumber;
    
    // Request context (if HTTP related)
    public String httpMethod;
    public String endpoint;
    public String ipAddress;
    public String userAgent;
    
    // Additional data
    public String metadata;         // JSON string for request body, params, etc.
    public LocalDateTime timestamp = LocalDateTime.now();
    
    // Error categorization
    public String errorCode;        // e.g., "DB_001", "AUTH_403"
    public String category;         // e.g., "DATABASE", "AUTHENTICATION", "VALIDATION"
    public Boolean resolved = false;
    public String resolution;       // How it was fixed/resolved
    
    // POS context
    public String terminalId;
    public String storeId;
}
