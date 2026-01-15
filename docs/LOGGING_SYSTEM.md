# 📊 Comprehensive Logging System Architecture

## Overview

CleanSlice Platform implements a **professional multi-tier logging infrastructure** with 5 specialized log types, designed for pharmaceutical compliance, performance monitoring, and complete audit trails.

## 🎯 Log Types

### 1. **Audit Logs** - Compliance & Traceability
**Purpose**: Track all user actions, system changes, and business transactions for regulatory compliance.

**Storage**: `audit_logs` table  
**Kafka Topics**: `audit.login`, `audit.crud`, `audit.error`, `audit.transaction`, `audit.security`, `audit.system`

**Fields**:
- User identification (userId, username, ipAddress)
- Action details (action, entityType, entityId)
- Change tracking (oldValue, newValue)
- POS context (terminalId, storeId, shiftId)
- Pharmaceutical tracking (prescriptionId, batchNumber, lotNumber)

**Use Cases**:
- Regulatory audits (who did what, when)
- Compliance reports (prescription tracking)
- Security investigations (unauthorized access)
- Data change history (before/after values)

---

### 2. **Access Logs** - HTTP Traffic Monitoring
**Purpose**: Monitor all HTTP requests/responses with timing and status codes.

**Storage**: `access_logs` table  
**Kafka Topic**: `logs.access`  
**Automatic**: ✅ Yes (via `AccessLogFilter`)

**Fields**:
- HTTP details (method, endpoint, statusCode, responseTimeMs)
- Request info (requestId, ipAddress, userAgent)
- User context (userId, username, sessionId)
- Response metrics (responseSize, contentType)

**Use Cases**:
- Performance analysis (slow requests >1s)
- API usage statistics (endpoint popularity)
- Error tracking (4xx, 5xx responses)
- User behavior analysis

**Query Examples**:
```bash
# Get slow requests (>1000ms)
GET /api/logs/access/slow?minMs=1000&page=0&size=50

# Get all 404 errors
GET /api/logs/access/status/404

# Get requests by user
GET /api/logs/access/user/user123

# Get error responses (4xx, 5xx)
GET /api/logs/access/errors
```

---

### 3. **Performance Logs** - Database & Operation Timing
**Purpose**: Track database operations, slow queries, and performance metrics.

**Storage**: `performance_logs` table  
**Kafka Topic**: `logs.performance`  
**Automatic**: ✅ Yes (via `DatabaseOperationLogger`)

**Fields**:
- Operation details (operation, operationType, durationMs)
- Slow query detection (isSlow, thresholdMs)
- Database metrics (sqlQuery, rowsAffected)
- Resource usage (memoryUsedMb, cpuPercent)

**Thresholds**:
- **Slow query**: >100ms for DB operations
- **Warning**: Operations taking >500ms

**Use Cases**:
- Slow query detection and optimization
- Database performance monitoring
- Resource usage tracking
- Performance regression detection

**Query Examples**:
```bash
# Get all slow operations
GET /api/logs/performance/slow

# Get operations taking >500ms
GET /api/logs/performance/min-duration/500

# Get average duration for specific operation
GET /api/logs/performance/stats/average/DB:INSERT:Product

# Get DB operations by service
GET /api/logs/performance/service/product-service
```

---

### 4. **Application Logs** - Business Events
**Purpose**: Structured logging for business workflows and application events.

**Storage**: `application_logs` table  
**Kafka Topic**: `logs.application`  
**Automatic**: ❌ No (manual via `LoggingHelper`)

**Fields**:
- Event details (eventType, message, level)
- Context (functionName, className, lineNumber)
- User tracking (userId, correlationId)
- Metadata (additional JSON data)

**Log Levels**: TRACE, DEBUG, INFO, WARN, ERROR, FATAL

**Use Cases**:
- Business process tracking
- Application debugging
- Feature usage analysis
- Custom business rules logging

**Usage Example**:
```java
@Inject
LoggingHelper loggingHelper;

// Log business event
loggingHelper.logApp(
    LogLevel.INFO,
    "Product creation started: Paracetamol 500mg",
    userContext.getCurrentUserId(),
    correlationId
);
```

**Query Examples**:
```bash
# Get logs by level
GET /api/logs/application/level/ERROR

# Search in messages
GET /api/logs/application/search?keyword=creation

# Get logs by correlation ID (trace request flow)
GET /api/logs/application/correlation/abc-123-xyz
```

---

### 5. **Error Logs** - Exception Tracking
**Purpose**: Automatically capture all uncaught exceptions with full stack traces.

**Storage**: `error_logs` table  
**Kafka Topic**: `logs.error`  
**Automatic**: ✅ Yes (via `GlobalExceptionLogger`)

**Fields**:
- Exception details (exceptionType, message, stackTrace)
- HTTP context (httpStatus, httpMethod, endpoint)
- User tracking (userId, sessionId)
- Error categorization (errorCode, errorCategory)

**Use Cases**:
- Production error monitoring
- Bug tracking and debugging
- Error rate analysis
- Customer support investigations

**Query Examples**:
```bash
# Get recent errors (last 20)
GET /api/logs/error/recent?limit=20

# Get errors by exception type
GET /api/logs/error/exception/ProductNotFoundException

# Get 500 errors
GET /api/logs/error/http-status/500

# Search in error messages/stack traces
GET /api/logs/error/search?keyword=NullPointerException
```

---

## 🔄 Automatic Logging

### How It Works (Like .NET SaveChanges Override)

#### 1. **AccessLogFilter** - HTTP Interception
```java
@Provider
public class AccessLogFilter implements ContainerRequestFilter, ContainerResponseFilter {
    // Automatically logs ALL HTTP requests/responses
    // - Captures timing, status codes, user info
    // - Detects slow requests (>1s)
    // - Zero configuration needed
}
```

**Triggers**: Every HTTP request to any endpoint  
**What's Logged**: Method, path, status, duration, user

#### 2. **DatabaseOperationLogger** - DB Operation Wrapper
```java
// Wraps DB operations with automatic timing
DatabaseOperationLogger.logPersist(product, 
    productRepository.save(product)
);

// Logs:
// - Operation type (INSERT/UPDATE/DELETE)
// - Duration in milliseconds
// - Slow query detection (>100ms)
```

**Triggers**: When you wrap repository calls  
**What's Logged**: Operation, entity, timing, slow flag

#### 3. **GlobalExceptionLogger** - Exception Catcher
```java
@Provider
public class GlobalExceptionLogger implements ExceptionMapper<Throwable> {
    // Catches ALL uncaught exceptions
    // - Full stack trace
    // - HTTP status mapping
    // - User context
}
```

**Triggers**: Any uncaught exception  
**What's Logged**: Exception type, message, stack trace, HTTP status

---

## 📡 Kafka Topics Structure

```
audit.login          → Login/logout events
audit.crud           → CRUD operations
audit.error          → Error/failure events
audit.transaction    → Business transactions
audit.security       → Security events
audit.system         → System events

logs.application     → Business/app logs
logs.error           → Exception logs
logs.access          → HTTP traffic logs
logs.performance     → DB operation logs
```

**Benefits**:
- Topic-based segregation for scalability
- Independent retention policies
- Parallel processing
- Event replay capabilities

---

## 🗄️ Database Schema

### Index Strategy

All log tables have optimized indexes for common queries:

**audit_logs**:
- `idx_audit_type` (auditType)
- `idx_audit_user` (userId)
- `idx_audit_timestamp` (timestamp)
- `idx_audit_correlation` (correlationId)
- `idx_audit_entity` (entityType, entityId)

**access_logs**:
- `idx_access_endpoint` (endpoint)
- `idx_access_status` (statusCode)
- `idx_access_timestamp` (timestamp)
- `idx_access_user` (userId)
- `idx_access_correlation` (correlationId)
- `idx_access_slow` (responseTimeMs) for slow query detection
- `idx_access_service` (serviceName)

**performance_logs**:
- `idx_perf_operation` (operation)
- `idx_perf_slow` (isSlow)
- `idx_perf_timestamp` (timestamp)
- `idx_perf_correlation` (correlationId)
- `idx_perf_duration` (durationMs)
- `idx_perf_service` (serviceName)

**application_logs**:
- `idx_app_level` (level)
- `idx_app_timestamp` (timestamp)
- `idx_app_user` (userId)
- `idx_app_correlation` (correlationId)
- `idx_app_service` (serviceName)

**error_logs**:
- `idx_error_type` (exceptionType)
- `idx_error_timestamp` (timestamp)
- `idx_error_http_status` (httpStatus)
- `idx_error_user` (userId)
- `idx_error_correlation` (correlationId)
- `idx_error_service` (serviceName)
- `idx_error_endpoint` (endpoint)

---

## 🔍 Query API Reference

### Common Query Parameters

All endpoints support:
- `page`: Page number (default: 0)
- `size`: Page size (default: 50)

### Audit Logs API

```bash
# Base path: /api/audit

GET /api/audit                              # Get all audit logs
GET /api/audit/type/CRUD                    # Get by type
GET /api/audit/user/{userId}                # Get by user
GET /api/audit/entity/{type}/{id}           # Get by entity
GET /api/audit/service/{serviceName}        # Get by service
GET /api/audit/correlation/{correlationId}  # Trace request
GET /api/audit/errors/recent               # Recent errors
GET /api/audit/security/recent             # Security events
GET /api/audit/stats/type/CRUD             # Count by type
```

### Application Logs API

```bash
# Base path: /api/logs/application

GET /api/logs/application                         # Get all
GET /api/logs/application/level/ERROR            # By log level
GET /api/logs/application/service/{name}         # By service
GET /api/logs/application/user/{userId}          # By user
GET /api/logs/application/correlation/{id}       # Trace request
GET /api/logs/application/search?keyword=xyz     # Search
GET /api/logs/application/date-range             # Date range
  ?from=2025-12-01T00:00:00
  &to=2025-12-06T23:59:59
GET /api/logs/application/stats/level/ERROR      # Count by level
```

### Error Logs API

```bash
# Base path: /api/logs/error

GET /api/logs/error                              # Get all
GET /api/logs/error/exception/ProductNotFoundException  # By exception type
GET /api/logs/error/service/{name}               # By service
GET /api/logs/error/user/{userId}                # By user
GET /api/logs/error/http-status/500              # By HTTP status
GET /api/logs/error/correlation/{id}             # Trace request
GET /api/logs/error/recent?limit=20              # Recent errors
GET /api/logs/error/search?keyword=null          # Search
GET /api/logs/error/stats/exception/NPE          # Count by type
GET /api/logs/error/stats/http-status/500        # Count by status
```

### Access Logs API

```bash
# Base path: /api/logs/access

GET /api/logs/access                             # Get all
GET /api/logs/access/method/POST                 # By HTTP method
GET /api/logs/access/endpoint?path=/api/products # By endpoint
GET /api/logs/access/status/404                  # By status code
GET /api/logs/access/service/{name}              # By service
GET /api/logs/access/user/{userId}               # By user
GET /api/logs/access/correlation/{id}            # Trace request
GET /api/logs/access/slow?minMs=1000             # Slow requests
GET /api/logs/access/errors                      # Error responses (4xx, 5xx)
GET /api/logs/access/stats/status/200            # Count by status
GET /api/logs/access/stats/method/GET            # Count by method
GET /api/logs/access/stats/slow?minMs=1000       # Count slow requests
```

### Performance Logs API

```bash
# Base path: /api/logs/performance

GET /api/logs/performance                        # Get all
GET /api/logs/performance/operation/DB:INSERT    # By operation
GET /api/logs/performance/service/{name}         # By service
GET /api/logs/performance/slow                   # Slow operations
GET /api/logs/performance/min-duration/500       # Min duration filter
GET /api/logs/performance/correlation/{id}       # Trace request
GET /api/logs/performance/search?keyword=Product # Search
GET /api/logs/performance/stats/slow             # Count slow ops
GET /api/logs/performance/stats/average/DB:INSERT # Average duration
```

---

## 💡 Best Practices

### 1. Use Correlation IDs for Request Tracing

```java
// Every request gets a correlation ID
String correlationId = UUID.randomUUID().toString();

// Use it across all log types
loggingHelper.logApp(LogLevel.INFO, "Start", userId, correlationId);
DatabaseOperationLogger.logPersist(entity, operation); // Auto includes
loggingHelper.logError(ex, userId, correlationId);

// Query all logs for a request
GET /api/logs/application/correlation/{correlationId}
GET /api/logs/access/correlation/{correlationId}
GET /api/logs/performance/correlation/{correlationId}
GET /api/logs/error/correlation/{correlationId}
```

### 2. Log Levels Strategy

- **TRACE**: Very detailed (dev only)
- **DEBUG**: Debugging info (dev/staging)
- **INFO**: Normal operations (production)
- **WARN**: Potential issues (production)
- **ERROR**: Errors requiring attention (production)
- **FATAL**: Critical failures (production)

### 3. Search & Filter Efficiently

```bash
# Date range for large datasets
GET /api/logs/error/date-range?from=2025-12-01T00:00:00&to=2025-12-06T23:59:59

# Use pagination for large results
GET /api/logs/access?page=0&size=100

# Specific filters over general queries
GET /api/logs/error/http-status/500  # Better than searching all errors
```

### 4. Monitor Performance

```bash
# Track slow operations daily
GET /api/logs/performance/slow?page=0&size=20

# Check slow request count
GET /api/logs/access/stats/slow?minMs=1000

# Get average duration trends
GET /api/logs/performance/stats/average/DB:INSERT:Product
```

---

## 🚀 Production Deployment

### Retention Policies

Recommended Kafka retention:
- **audit.*** topics: 90+ days (compliance)
- **logs.*** topics: 30 days (operational)

Database archiving:
- Archive logs older than 90 days to cold storage
- Keep indexes up-to-date
- Monitor table sizes

### Monitoring Alerts

Set up alerts for:
- Error log spike (>10 errors/min)
- Slow query count increase (>5 slow/min)
- 500 error responses (any occurrence)
- Kafka consumer lag (>1000 messages)

### Scaling Considerations

- **Kafka**: Partition by service name for parallel processing
- **Database**: Partition tables by month for large datasets
- **Elasticsearch**: Consider for full-text search at scale
- **Grafana**: Visualize metrics from log statistics

---

## 📚 Code Examples

### Manual Application Logging

```java
@Inject
LoggingHelper loggingHelper;

@Inject
UserContext userContext;

public void businessOperation() {
    String correlationId = UUID.randomUUID().toString();
    
    // Start
    loggingHelper.logApp(
        LogLevel.INFO,
        "Business operation started",
        userContext.getCurrentUserId(),
        correlationId
    );
    
    try {
        // Business logic
        performOperation();
        
        // Success
        loggingHelper.logApp(
            LogLevel.INFO,
            "Operation completed successfully",
            userContext.getCurrentUserId(),
            correlationId
        );
    } catch (Exception ex) {
        // Error (automatic via GlobalExceptionLogger)
        throw ex; // Will be logged automatically
    }
}
```

### Automatic DB Logging

```java
@WithTransaction
public Uni<Product> createProduct(CreateProduct request) {
    Product product = mapper.toEntity(request);
    
    // Wrap with automatic performance logging
    return DatabaseOperationLogger.logPersist(product,
        productRepository.save(product)
    )
    .onItem().invoke(saved -> {
        // Publish audit event
        publishAuditEvent("CREATE", saved.RowId);
    });
}
```

### Query Logs Programmatically

```java
@Inject
QueryErrorLogsUseCase errorLogsUseCase;

// Get recent errors
Uni<List<ErrorLog>> recentErrors = errorLogsUseCase.getRecentErrors(20);

// Get errors by service
Uni<List<ErrorLog>> serviceErrors = 
    errorLogsUseCase.getLogsByService("product-service", 0, 50);

// Count 500 errors
Uni<Long> count500 = errorLogsUseCase.countByHttpStatus(500);
```

---

## 🎓 Summary

**Automatic Logging** (Zero Configuration):
- ✅ All HTTP requests (AccessLogFilter)
- ✅ All exceptions (GlobalExceptionLogger)
- ✅ DB operations when wrapped (DatabaseOperationLogger)

**Manual Logging** (When Needed):
- Business events (LoggingHelper.logApp)
- Custom metrics (LoggingHelper.logPerf)

**Query Capabilities**:
- REST APIs for all log types
- Pagination & filtering
- Correlation ID tracing
- Statistics & aggregations

**Production Ready**:
- Kafka event streaming
- PostgreSQL persistence
- Indexed for performance
- Scalable architecture

---

**Built with ❤️ for pharmaceutical compliance and operational excellence**
