# Automatic Logging Guide

## 📋 Overview

This guide covers **automatic logging** features that work like .NET's `SaveChanges()` override - capturing logs without manual intervention.

## 🎯 Three Types of Auto-Logging

### 1. Database Operation Logging (`DatabaseOperationLogger`)
**Like .NET's `DbContext.SaveChanges()` override**

Automatically tracks all database INSERT/UPDATE/DELETE operations with timing.

```java
// Before - manual
return productRepository.save(product);

// After - automatic DB logging
return DatabaseOperationLogger.logPersist(product, 
    productRepository.save(product)
);
```

**What it logs:**
- ✅ Operation type (INSERT, UPDATE, DELETE)
- ✅ Entity name
- ✅ Duration in milliseconds
- ✅ Slow query detection (> 100ms)
- ✅ Automatic error logging on DB failures

**Example console output:**
```
⚡ DB operation: INSERT Product took 45ms
🐌 Slow DB operation: UPDATE Product took 250ms
❌ DB operation failed: DELETE Category after 120ms
```

**Performance logs sent to Kafka:**
```json
{
  "serviceName": "product-service",
  "operation": "DB:INSERT:Product",
  "durationMs": 250,
  "isSlow": true,
  "operationType": "DATABASE"
}
```

---

### 2. HTTP Access Logging (`AccessLogFilter`)
**JAX-RS Filter - works automatically for ALL endpoints**

Captures every HTTP request/response without any code changes.

```java
// NO CODE NEEDED - automatically logs ALL endpoints
@GET
@Path("/products")
public Uni<List<Product>> getAllProducts() {
    return productService.getAllProducts();
}
```

**What it logs:**
- ✅ HTTP method (GET, POST, PUT, DELETE)
- ✅ Path and query string
- ✅ Status code
- ✅ Response time
- ✅ User info from headers (X-User-Id, X-Username)
- ✅ Request/Response correlation ID

**Example console output:**
```
📨 Incoming: GET /api/products [uuid-123]
✅ GET /api/products - 200 (45ms)
⚠️ POST /api/products - 400 (23ms)
❌ GET /api/products/999 - 500 (150ms)
🐌 POST /api/products - 200 (1200ms) SLOW
```

**Access logs sent to Kafka:**
```json
{
  "serviceName": "product-service",
  "httpMethod": "POST",
  "endpoint": "/api/products",
  "statusCode": 201,
  "responseTimeMs": 45,
  "userId": "user123",
  "correlationId": "uuid-123"
}
```

---

### 3. Global Exception Logging (`GlobalExceptionLogger`)
**JAX-RS ExceptionMapper - catches ALL uncaught exceptions**

Automatically logs any exception that bubbles up to the HTTP layer.

```java
// NO CODE NEEDED - automatically catches exceptions
@GET
@Path("/products/{id}")
public Uni<Product> getProduct(String id) {
    // If this throws, GlobalExceptionLogger catches it automatically
    return productService.getProductById(id);
}
```

**What it logs:**
- ✅ Exception type and message
- ✅ Full stack trace
- ✅ Automatic HTTP status code mapping
- ✅ Sends structured error to Kafka
- ✅ Returns proper JSON error response

**Automatic status code mapping:**
```java
NotFoundException        → 404
UnauthorizedException   → 401
ForbiddenException      → 403
ValidationException     → 400
IllegalArgumentException → 400
ConflictException       → 409
Other exceptions        → 500
```

**Example console output:**
```
❌ Unhandled exception: Product not found with id: 999
```

**Error logs sent to Kafka:**
```json
{
  "serviceName": "product-service",
  "exceptionType": "ProductNotFoundException",
  "message": "Product not found with id: 999",
  "stackTrace": "...",
  "category": "VALIDATION",
  "endpoint": "/api/products/999"
}
```

---

## 🔧 Setup & Usage

### Automatic Setup

The filters are **auto-discovered** by Quarkus via `@Provider` annotation:

```java
@Provider  // ← This makes it automatic
public class AccessLogFilter implements ContainerRequestFilter, ContainerResponseFilter {
    // Automatically applied to ALL endpoints
}

@Provider  // ← This makes it automatic
public class GlobalExceptionLogger implements ExceptionMapper<Exception> {
    // Automatically catches ALL exceptions
}
```

### Manual Usage for Database Logging

Update your service methods to wrap DB operations:

```java
@ApplicationScoped
public class ProductService {

    @WithTransaction
    public Uni<GetProduct> createProduct(CreateProduct request) {
        Product product = productMapper.toEntity(request);
        
        // Wrap with automatic logging
        return DatabaseOperationLogger.logPersist(product, 
            productRepository.save(product)
                .call(p -> p.flush())
        )
        .onItem().transform(productMapper::toResponse);
    }
    
    @WithTransaction
    public Uni<GetProduct> updateProduct(String id, UpdateProduct request) {
        return productRepository.findById(id)
            .onItem().invoke(p -> productMapper.updateEntity(request, p))
            .call(product -> 
                // Wrap update with logging
                DatabaseOperationLogger.logUpdate(product, 
                    Uni.createFrom().item(product)
                )
            )
            .onItem().transform(productMapper::toResponse);
    }
    
    @WithTransaction
    public Uni<Void> deleteProduct(String id) {
        return productRepository.findById(id)
            .onItem().transformToUni(product ->
                // Wrap delete with logging
                DatabaseOperationLogger.logDelete("Product",
                    productRepository.delete(product)
                )
            );
    }
}
```

---

## 📊 What Gets Logged Automatically

| Event | Auto-Logged? | Where | Log Type |
|-------|-------------|-------|----------|
| HTTP Request arrives | ✅ Yes | AccessLogFilter | Access |
| HTTP Response sent | ✅ Yes | AccessLogFilter | Access |
| Uncaught exception | ✅ Yes | GlobalExceptionLogger | Error |
| Database INSERT | 🔄 Wrapped | DatabaseOperationLogger | Performance |
| Database UPDATE | 🔄 Wrapped | DatabaseOperationLogger | Performance |
| Database DELETE | 🔄 Wrapped | DatabaseOperationLogger | Performance |
| Slow query (>100ms) | ✅ Yes | DatabaseOperationLogger | Performance |
| Manual logs | ❌ No | Use LoggingHelper | Application |

---

## 🎯 Comparison with .NET

### .NET Entity Framework
```csharp
public class MyDbContext : DbContext {
    public override int SaveChanges() {
        // Automatically logs all changes
        LogChanges();
        return base.SaveChanges();
    }
}
```

### Quarkus/Hibernate Reactive
```java
// Option 1: Wrap operations
return DatabaseOperationLogger.logPersist(entity, 
    repository.save(entity)
);

// Option 2: Use interceptor (future enhancement)
// @Interceptor + @AroundInvoke for automatic wrapping
```

---

## 🔍 Example: Complete Request Flow

When user calls `POST /api/products`:

```
1. 📨 AccessLogFilter.filter() 
   → Logs: "Incoming POST /api/products"

2. 🔄 ProductService.createProduct()
   → Wrapped with DatabaseOperationLogger
   
3. 💾 DatabaseOperationLogger.logPersist()
   → Logs: "DB operation: INSERT Product took 45ms"
   → Sends PerformanceLog to Kafka
   
4. ✅ AccessLogFilter.filter()
   → Logs: "POST /api/products - 201 (50ms)"
   → Sends AccessLog to Kafka

If error occurs:
5. ❌ GlobalExceptionLogger.toResponse()
   → Logs: "Unhandled exception: ..."
   → Sends ErrorLog to Kafka
   → Returns 500 JSON response
```

---

## ⚙️ Configuration

### Adjust slow query threshold

```java
// In DatabaseOperationLogger.java
if (duration > 100) {  // ← Change this threshold
    logger.logPerf(..., true);  // Mark as slow
}
```

### Customize status code mapping

```java
// In GlobalExceptionLogger.java
private int determineStatusCode(Exception exception) {
    // Add your custom mappings
    if (exception instanceof MyCustomException) return 422;
    // ...
}
```

### Disable specific filters

```java
// Add to application.yml
quarkus:
  http:
    filter:
      access-log:
        enabled: false  # Disable access logging
```

---

## 📝 Best Practices

1. **Always wrap DB operations** with `DatabaseOperationLogger` for consistent timing
2. **Don't catch exceptions** unless you handle them - let GlobalExceptionLogger catch them
3. **Set appropriate thresholds** for slow query detection based on your SLA
4. **Use correlation IDs** - AccessLogFilter generates them automatically
5. **Check logs in Kafka** - all auto-logs go to respective topics

---

## 🚀 Future Enhancements

- [ ] CDI Interceptor for automatic DB logging (no wrapping needed)
- [ ] Aspect-Oriented Programming (AOP) for method-level auto-logging
- [ ] Configurable sampling rate (log 1 in N requests)
- [ ] Request body/response logging (with sensitive data masking)
- [ ] Automatic correlation with distributed tracing (OpenTelemetry)

---

## 📞 Related Docs

- [Logging System](./LOGGING_SYSTEM.md)
- [Audit Logging](./AUDIT_LOGGING_GUIDE.md)
