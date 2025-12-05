package share.dto;

import share.enums.LogLevel;
import java.time.LocalDateTime;

/**
 * DTO for general application logs
 * Used for business logic, workflow tracking, general debugging
 */
public class ApplicationLog {
    public LogLevel level;
    public String serviceName;
    public String logger;           // Class/package name
    public String message;
    public String thread;
    public String method;
    public String className;
    
    // Context
    public String userId;
    public String username;
    public String sessionId;
    public String correlationId;
    public String transactionId;
    
    // Location
    public String fileName;
    public Integer lineNumber;
    
    // Additional data
    public String metadata;         // JSON string for extra context
    public LocalDateTime timestamp = LocalDateTime.now();
    
    // POS context
    public String terminalId;
    public String storeId;
    public String shiftId;
}
