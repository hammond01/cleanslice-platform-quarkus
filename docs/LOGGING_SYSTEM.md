# Comprehensive Logging System

## 📋 Overview

This document describes the centralized logging infrastructure for the pharmaceutical POS system. The logging system provides structured, searchable logs across all services for debugging, monitoring, compliance, and performance analysis.

## 🎯 Logging Types

The system supports **5 types of logs**, each with a specific purpose:

| Log Type | Topic | Purpose | Retention |
|----------|-------|---------|-----------|
| **Audit** | `audit.*` | Compliance, regulatory, user actions | Long-term (7+ years) |
| **Application** | `logs.application` | Business logic, workflow tracking | 30-90 days |
| **Error** | `logs.error` | Exceptions, failures, debugging | 90 days |
| **Access** | `logs.access` | HTTP requests, API calls | 30 days |
| **Performance** | `logs.performance` | Metrics, slow operations | 30 days |

---

## 📊 Log Schemas

### 1. Application Log
```java
ApplicationLog {
    LogLevel level;              // TRACE, DEBUG, INFO, WARN, ERROR, FATAL
    String serviceName;
    String logger;               // Class/package name
    String message;
    String thread;
    String method;
    String className;
    
    // Context
    String userId;
    String username;
    String sessionId;
    String correlationId;
    String transactionId;
    
    // Location
    String fileName;
    Integer lineNumber;
    
    // Additional
    String metadata;             // JSON
    LocalDateTime timestamp;
    
    // POS context
    String terminalId;
    String storeId;
    String shiftId;
}
```

**Use cases:**
- Business flow tracking: "Order processing started"
- State changes: "Inventory updated for product X"
- General debugging information
- Non-error warnings

### 2. Error Log
```java
ErrorLog {
    LogLevel level;              // ERROR or FATAL
    String serviceName;
    String exceptionType;        // e.g., "NullPointerException"
    String message;
    String stackTrace;
    String rootCause;
    
    // Context when error occurred
    String userId;
    String username;
    String sessionId;
    String correlationId;
    String transactionId;
    
    // Location
    String className;
    String method;
    String fileName;
    Integer lineNumber;
    
    // HTTP context (if applicable)
    String httpMethod;
    String endpoint;
    String ipAddress;
    String userAgent;
    
    // Categorization
    String errorCode;            // e.g., "DB_001", "AUTH_403"
    String category;             // DATABASE, AUTHENTICATION, VALIDATION, etc.
    Boolean resolved;
    String resolution;
    
    LocalDateTime timestamp;
}
```

**Use cases:**
- Exception tracking with full stack traces
- Error categorization for analytics
- Debugging production issues
- Alerting on critical errors

### 3. Access Log
```java
AccessLog {
    String serviceName;
    String httpMethod;           // GET, POST, PUT, DELETE
    String endpoint;
    String path;
    String queryString;
    
    // Request
    String requestId;
    String ipAddress;
    String userAgent;
    String referer;
    String origin;
    Integer requestSize;         // bytes
    
    // Response
    Integer statusCode;          // 200, 404, 500, etc.
    Integer responseSize;
    String contentType;
    
    // Timing
    Long responseTimeMs;
    LocalDateTime requestTime;
    LocalDateTime responseTime;
    
    // User
    String userId;
    String username;
    String sessionId;
    String correlationId;
    
    // Security
    String authMethod;           // Bearer, Basic, Session
    Boolean authenticated;
    
    LocalDateTime timestamp;
}
```

**Use cases:**
- API usage analytics
- Response time monitoring
- Security auditing (failed auth attempts)
- Traffic analysis

### 4. Performance Log
```java
PerformanceLog {
    String serviceName;
    String operation;            // e.g., "createProduct", "queryCategories"
    String operationType;        // DATABASE, HTTP, KAFKA, BUSINESS_LOGIC
    
    // Timing
    Long durationMs;
    Long thresholdMs;
    Boolean isSlow;
    
    // Resources
    Long memoryUsedMb;
    Double cpuPercent;
    Integer threadCount;
    
    // Database metrics
    String sqlQuery;
    Long queryTimeMs;
    Integer rowsAffected;
    Integer connectionPoolSize;
    
    // HTTP metrics
    String httpMethod;
    String endpoint;
    Integer statusCode;
    
    String correlationId;
    LocalDateTime timestamp;
}
```

**Use cases:**
- Slow query detection
- Performance regression analysis
- Resource usage monitoring
- Optimization opportunities

---

## 🔧 Usage Guide

### Using LoggingHelper

Each service has a `LoggingHelper` class for easy logging:

```java
@Inject
LoggingHelper loggingHelper;

// Application log
loggingHelper.logApp(LogLevel.INFO, "Product created successfully", userId, correlationId);

// Error log
try {
    // ... code
} catch (Exception e) {
    loggingHelper.logError(e, userId, correlationId);
}

// Access log (usually in HTTP filter/interceptor)
loggingHelper.logAccess("POST", "/api/products", 201, 150L, userId);

// Performance log
long start = System.currentTimeMillis();
// ... operation
long duration = System.currentTimeMillis() - start;
loggingHelper.logPerf("createProduct", duration, duration > 1000);
```

### Example: Product Creation with Full Logging

```java
@POST
@Path("/products")
public Uni<Response> createProduct(ProductRequest request, @Context HttpHeaders headers) {
    String correlationId = UUID.randomUUID().toString();
    long startTime = System.currentTimeMillis();
    String userId = extractUserId(headers);
    
    // Application log: start
    loggingHelper.logApp(LogLevel.INFO, "Creating product: " + request.getName(), 
        userId, correlationId);
    
    return productService.createProduct(request)
        .onItem().invoke(product -> {
            // Application log: success
            loggingHelper.logApp(LogLevel.INFO, "Product created: " + product.getRowId(), 
                userId, correlationId);
            
            // Performance log
            long duration = System.currentTimeMillis() - startTime;
            loggingHelper.logPerf("createProduct", duration, duration > 500);
            
            // Access log
            loggingHelper.logAccess("POST", "/api/products", 201, duration, userId);
        })
        .onFailure().invoke(error -> {
            // Error log
            loggingHelper.logError(error, userId, correlationId);
            
            // Access log with error status
            long duration = System.currentTimeMillis() - startTime;
            loggingHelper.logAccess("POST", "/api/products", 500, duration, userId);
        })
        .map(product -> Response.status(201).entity(product).build());
}
```

---

## 🏗️ Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Product        │     │  Category       │     │  Other          │
│  Service        │     │  Service        │     │  Services       │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         │ Publish logs via Kafka                       │
         ▼                       ▼                       ▼
    ┌────────────────────────────────────────────────────────┐
    │              Apache Kafka (Message Bus)                │
    │  Topics: logs.application, logs.error,                 │
    │          logs.access, logs.performance                 │
    └───────────────────────┬────────────────────────────────┘
                            │
                            │ Consume
                            ▼
                   ┌─────────────────┐
                   │  Audit/Logging  │
                   │  Service        │
                   │  (Port 8011)    │
                   └────────┬────────┘
                            │
                            │ Store
                            ▼
                   ┌─────────────────┐
                   │   PostgreSQL    │
                   │   audit_db      │
                   │                 │
                   │  - audit_logs         │
                   │  - application_logs   │
                   │  - error_logs         │
                   │  - access_logs        │
                   │  - performance_logs   │
                   └─────────────────┘
```

---

## 📦 Database Schema

### application_logs
```sql
CREATE TABLE application_logs (
    id BIGSERIAL PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    logger VARCHAR(255),
    message TEXT,
    thread VARCHAR(100),
    method VARCHAR(255),
    class_name VARCHAR(255),
    user_id VARCHAR(100),
    username VARCHAR(255),
    session_id VARCHAR(100),
    correlation_id VARCHAR(100),
    transaction_id VARCHAR(100),
    file_name VARCHAR(255),
    line_number INTEGER,
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    terminal_id VARCHAR(50),
    store_id VARCHAR(50),
    shift_id VARCHAR(50)
);

CREATE INDEX idx_app_level ON application_logs(level);
CREATE INDEX idx_app_service ON application_logs(service_name);
CREATE INDEX idx_app_timestamp ON application_logs(timestamp);
CREATE INDEX idx_app_correlation ON application_logs(correlation_id);
CREATE INDEX idx_app_user ON application_logs(user_id);
```

### error_logs
```sql
CREATE TABLE error_logs (
    id BIGSERIAL PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    exception_type VARCHAR(255),
    message TEXT,
    stack_trace TEXT,
    root_cause TEXT,
    user_id VARCHAR(100),
    username VARCHAR(255),
    session_id VARCHAR(100),
    correlation_id VARCHAR(100),
    transaction_id VARCHAR(100),
    class_name VARCHAR(255),
    method VARCHAR(255),
    file_name VARCHAR(255),
    line_number INTEGER,
    http_method VARCHAR(10),
    endpoint VARCHAR(500),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    error_code VARCHAR(50),
    category VARCHAR(100),
    resolved BOOLEAN DEFAULT FALSE,
    resolution TEXT,
    terminal_id VARCHAR(50),
    store_id VARCHAR(50)
);

CREATE INDEX idx_error_level ON error_logs(level);
CREATE INDEX idx_error_service ON error_logs(service_name);
CREATE INDEX idx_error_type ON error_logs(exception_type);
CREATE INDEX idx_error_timestamp ON error_logs(timestamp);
CREATE INDEX idx_error_correlation ON error_logs(correlation_id);
CREATE INDEX idx_error_category ON error_logs(category);
CREATE INDEX idx_error_resolved ON error_logs(resolved);
```

### access_logs
```sql
CREATE TABLE access_logs (
    id BIGSERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    endpoint VARCHAR(500),
    path VARCHAR(500),
    query_string VARCHAR(1000),
    request_id VARCHAR(100),
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    referer VARCHAR(500),
    origin VARCHAR(255),
    request_size INTEGER,
    status_code INTEGER,
    response_size INTEGER,
    content_type VARCHAR(100),
    response_time_ms BIGINT,
    request_time TIMESTAMP,
    response_time TIMESTAMP,
    user_id VARCHAR(100),
    username VARCHAR(255),
    session_id VARCHAR(100),
    correlation_id VARCHAR(100),
    auth_method VARCHAR(50),
    authenticated BOOLEAN DEFAULT FALSE,
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    terminal_id VARCHAR(50),
    store_id VARCHAR(50)
);

CREATE INDEX idx_access_service ON access_logs(service_name);
CREATE INDEX idx_access_method ON access_logs(http_method);
CREATE INDEX idx_access_status ON access_logs(status_code);
CREATE INDEX idx_access_timestamp ON access_logs(timestamp);
CREATE INDEX idx_access_user ON access_logs(user_id);
CREATE INDEX idx_access_ip ON access_logs(ip_address);
CREATE INDEX idx_access_endpoint ON access_logs(endpoint);
```

### performance_logs
```sql
CREATE TABLE performance_logs (
    id BIGSERIAL PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    operation VARCHAR(255) NOT NULL,
    operation_type VARCHAR(50),
    duration_ms BIGINT,
    threshold_ms BIGINT,
    is_slow BOOLEAN DEFAULT FALSE,
    memory_used_mb BIGINT,
    cpu_percent DOUBLE PRECISION,
    thread_count INTEGER,
    sql_query TEXT,
    query_time_ms BIGINT,
    rows_affected INTEGER,
    connection_pool_size INTEGER,
    http_method VARCHAR(10),
    endpoint VARCHAR(500),
    status_code INTEGER,
    user_id VARCHAR(100),
    correlation_id VARCHAR(100),
    transaction_id VARCHAR(100),
    metadata TEXT,
    timestamp TIMESTAMP NOT NULL,
    terminal_id VARCHAR(50),
    store_id VARCHAR(50)
);

CREATE INDEX idx_perf_service ON performance_logs(service_name);
CREATE INDEX idx_perf_operation ON performance_logs(operation);
CREATE INDEX idx_perf_type ON performance_logs(operation_type);
CREATE INDEX idx_perf_timestamp ON performance_logs(timestamp);
CREATE INDEX idx_perf_slow ON performance_logs(is_slow);
CREATE INDEX idx_perf_duration ON performance_logs(duration_ms);
```

---

## 🔍 Querying Logs

### Find slow operations
```sql
SELECT operation, AVG(duration_ms) as avg_ms, MAX(duration_ms) as max_ms, COUNT(*)
FROM performance_logs
WHERE timestamp > NOW() - INTERVAL '1 hour'
  AND is_slow = true
GROUP BY operation
ORDER BY avg_ms DESC;
```

### Find error patterns
```sql
SELECT category, exception_type, COUNT(*) as occurrences
FROM error_logs
WHERE timestamp > NOW() - INTERVAL '24 hours'
GROUP BY category, exception_type
ORDER BY occurrences DESC
LIMIT 10;
```

### API usage statistics
```sql
SELECT http_method, endpoint, 
       COUNT(*) as requests,
       AVG(response_time_ms) as avg_response_ms,
       PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY response_time_ms) as p95_ms
FROM access_logs
WHERE timestamp > NOW() - INTERVAL '1 hour'
GROUP BY http_method, endpoint
ORDER BY requests DESC;
```

### Trace user journey by correlationId
```sql
-- Find all logs for a specific operation
SELECT 'app' as type, timestamp, level, message FROM application_logs WHERE correlation_id = '...'
UNION ALL
SELECT 'error', timestamp, level::text, message FROM error_logs WHERE correlation_id = '...'
UNION ALL
SELECT 'access', timestamp, 'INFO', http_method || ' ' || endpoint FROM access_logs WHERE correlation_id = '...'
UNION ALL
SELECT 'perf', timestamp, 'INFO', operation || ': ' || duration_ms || 'ms' FROM performance_logs WHERE correlation_id = '...'
ORDER BY timestamp;
```

---

## 🎯 Best Practices

1. **Always use correlationId** for tracing requests across services
2. **Log at appropriate levels**:
   - `TRACE`: Very detailed (rarely used in production)
   - `DEBUG`: Detailed debugging info
   - `INFO`: Important business events
   - `WARN`: Potentially harmful situations
   - `ERROR`: Errors that need attention
   - `FATAL`: Critical errors

3. **Include context**: userId, terminalId, storeId for pharmaceutical compliance
4. **Use structured metadata**: Store additional data as JSON in metadata field
5. **Don't log sensitive data**: passwords, credit cards, personal health info
6. **Set performance thresholds**: Mark operations as slow based on business requirements
7. **Categorize errors**: Use consistent error codes and categories

---

## 🚀 Future Enhancements

- [ ] Log aggregation with Elasticsearch/Loki
- [ ] Real-time dashboards with Grafana
- [ ] Alerting on error rates/slow operations
- [ ] Log retention policies and archival
- [ ] Correlation with distributed tracing (Jaeger/Zipkin)
- [ ] Machine learning for anomaly detection
- [ ] REST APIs for log querying
- [ ] Export logs to external SIEM systems

---

## 📞 Related Documentation

- [Audit Logging Guide](./AUDIT_LOGGING_GUIDE.md)
- [Clean Architecture](./CLEAN_ARCHITECTURE.md)
- [README](../README.md)
