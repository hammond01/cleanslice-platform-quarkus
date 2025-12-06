param(
    [Parameter(Mandatory=$true)]
    [string]$moduleName
)

# Base paths
$root = Get-Location
$servicesDir = Join-Path $root "services"
$moduleDir = Join-Path $servicesDir $moduleName

# 1) Create full folder structure (Clean Architecture + DDD + Logging)
$folders = @(
    # Application Layer
    "src/main/java/application/dto",
    "src/main/java/application/mapper",
    "src/main/java/application/port/inbound",
    "src/main/java/application/port/outbound",
    "src/main/java/application/service",
    "src/main/java/application/usecase",

    # Domain Layer
    "src/main/java/domain/entity",
    "src/main/java/domain/enums",
    "src/main/java/domain/event",
    "src/main/java/domain/exception",

    # Infrastructure Layer
    "src/main/java/infrastructure/logging",
    "src/main/java/infrastructure/messaging/adapter",
    "src/main/java/infrastructure/persistence",
    "src/main/java/infrastructure/web",

    # Presentation Layer
    "src/main/java/presentation/rest",

    # Resources
    "src/main/resources",
    
    # Test folders
    "src/test/java/application",
    "src/test/java/domain",
    "src/test/java/infrastructure",
    "src/test/resources"
)

foreach ($f in $folders) {
    New-Item -ItemType Directory -Force -Path "$moduleDir/$f" | Out-Null
}

# 2) build.gradle.kts
$gradle = @"
plugins {
    id("java")
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val mapstructVersion = "1.5.5.Final"

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.29.4"))

    // Quarkus Core
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")
    
    // Reactive Database
    implementation("io.quarkus:quarkus-hibernate-reactive-panache")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    
    // Kafka Messaging
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging-kafka")
    
    // Jackson for JSON
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    
    // MapStruct for DTO mapping
    implementation("org.mapstruct:mapstruct:`${mapstructVersion}")
    annotationProcessor("org.mapstruct:mapstruct-processor:`${mapstructVersion}")
    
    // Validation
    implementation("io.quarkus:quarkus-hibernate-validator")
    
    // Config & OpenAPI
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    
    // Health & Metrics
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
    
    // Testing
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

group = "honeybee"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
"@
Set-Content "$moduleDir/build.gradle.kts" $gradle -Encoding UTF8

# 3) BaseEntity.java
$baseEntity = @"
package domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    public LocalDateTime createdAt;

    @Column(name = "created_by", updatable = false)
    public String createdBy;

    @Column(name = "last_modified_at")
    public LocalDateTime lastModifiedAt;

    @Column(name = "last_modified_by")
    public String lastModifiedBy;

    @Column(name = "deleted_at")
    public LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    public String deletedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
"@
Set-Content "$moduleDir/src/main/java/domain/entity/BaseEntity.java" $baseEntity -Encoding UTF8


# 4) BaseEntityWithNumber.java (Number = PK)
$baseEntityNumber = @"
package domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntityWithNumber extends BaseEntity {

    @Id
    @Column(name = "number", nullable = false, unique = true)
    public String number;

    @Column(name = "locked_at")
    public LocalDateTime lockedAt;

    @Column(name = "locked_by")
    public String lockedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "modification_status", nullable = false)
    public ModificationStatus modificationStatus = ModificationStatus.ACTIVE;

    public boolean isLocked() { return lockedAt != null; }

    public void lock(String user) {
        lockedAt = LocalDateTime.now();
        lockedBy = user;
        modificationStatus = ModificationStatus.LOCKED;
    }

    public void unlock() {
        lockedAt = null;
        lockedBy = null;
        modificationStatus = ModificationStatus.ACTIVE;
    }
}
"@
Set-Content "$moduleDir/src/main/java/domain/entity/BaseEntityWithNumber.java" $baseEntityNumber -Encoding UTF8


# 5) ModificationStatus.java
$enum = @"
package domain.enums;

public enum ModificationStatus {
    ACTIVE,
    LOCKED,
    DELETED,
    DRAFT,
    ARCHIVED
}
"@
Set-Content "$moduleDir/src/main/java/domain/enums/ModificationStatus.java" $enum -Encoding UTF8

# 6) application.yml with full configuration
$yaml = @"
quarkus:
  application:
    name: $moduleName
  http:
    port: 0
  
  # Logging configuration
  log:
    level: INFO
    console:
      enable: true
      format: "%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c{3.}] (%t) %s%e%n"
      level: INFO
  
  datasource:
    db-kind: postgresql
    username: postgres
    password: postgres
    reactive:
      url: postgresql://localhost:5432/${moduleName}_db
  
  hibernate-orm:
    database:
      generation: update
    log:
      sql: true
  
  devservices:
    enabled: false
  
  kafka:
    devservices:
      enabled: false
  
  swagger-ui:
    always-include: true
  
  smallrye-openapi:
    path: /openapi

mp:
  messaging:
    connector:
      smallrye-kafka:
        bootstrap:
          servers: localhost:29092
    
    outgoing:
      audit-crud:
        connector: smallrye-kafka
        topic: audit.crud
        value:
          serializer: org.apache.kafka.common.serialization.StringSerializer
      
      audit-error:
        connector: smallrye-kafka
        topic: audit.error
        value:
          serializer: org.apache.kafka.common.serialization.StringSerializer
      
      logs-application:
        connector: smallrye-kafka
        topic: logs.application
        value:
          serializer: org.apache.kafka.common.serialization.StringSerializer
      
      logs-error:
        connector: smallrye-kafka
        topic: logs.error
        value:
          serializer: org.apache.kafka.common.serialization.StringSerializer
      
      logs-access:
        connector: smallrye-kafka
        topic: logs.access
        value:
          serializer: org.apache.kafka.common.serialization.StringSerializer
      
      logs-performance:
        connector: smallrye-kafka
        topic: logs.performance
        value:
          serializer: org.apache.kafka.common.serialization.StringSerializer
"@
Set-Content "$moduleDir/src/main/resources/application.yml" $yaml -Encoding UTF8

# 7) LoggingHelper.java
$loggingHelper = @"
package infrastructure.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import share.dto.*;
import share.enums.LogLevel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Centralized logging helper for publishing structured logs to Kafka
 * Provides simplified API for logging application events, errors, access, and performance
 */
@ApplicationScoped
public class LoggingHelper {

    @Inject
    @Channel("logs-application")
    Emitter<String> applicationLogEmitter;

    @Inject
    @Channel("logs-error")
    Emitter<String> errorLogEmitter;

    @Inject
    @Channel("logs-access")
    Emitter<String> accessLogEmitter;

    @Inject
    @Channel("logs-performance")
    Emitter<String> performanceLogEmitter;

    @Inject
    ObjectMapper objectMapper;

    private String serviceName;

    @PostConstruct
    void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        serviceName = System.getProperty("quarkus.application.name", "unknown-service");
    }

    public void logApp(LogLevel level, String message, String userId, String correlationId) {
        ApplicationLog log = new ApplicationLog();
        log.level = level;
        log.serviceName = serviceName;
        log.message = message;
        log.userId = userId;
        log.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        log.thread = Thread.currentThread().getName();
        publishLog(applicationLogEmitter, log);
    }

    public void logError(Throwable ex, String userId, String correlationId) {
        ErrorLog log = new ErrorLog();
        log.serviceName = serviceName;
        log.exceptionType = ex.getClass().getName();
        log.message = ex.getMessage();
        log.stackTrace = getStackTrace(ex);
        log.userId = userId;
        log.correlationId = correlationId != null ? correlationId : UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publishLog(errorLogEmitter, log);
    }

    public void logAccess(String method, String endpoint, int status, long responseMs, String userId) {
        AccessLog log = new AccessLog();
        log.serviceName = serviceName;
        log.httpMethod = method;
        log.endpoint = endpoint;
        log.statusCode = status;
        log.responseTimeMs = responseMs;
        log.userId = userId;
        log.correlationId = UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publishLog(accessLogEmitter, log);
    }

    public void logPerf(String operation, long durationMs, boolean isSlow) {
        PerformanceLog log = new PerformanceLog();
        log.serviceName = serviceName;
        log.operation = operation;
        log.durationMs = durationMs;
        log.isSlow = isSlow;
        log.correlationId = UUID.randomUUID().toString();
        log.timestamp = LocalDateTime.now();
        publishLog(performanceLogEmitter, log);
    }

    private <T> void publishLog(Emitter<String> emitter, T log) {
        try {
            String json = objectMapper.writeValueAsString(log);
            Log.debugf("📤 Publishing log to Kafka: %s", log.getClass().getSimpleName());
            emitter.send(Message.of(json)
                .addMetadata(OutgoingKafkaRecordMetadata.<String>builder()
                    .withKey(UUID.randomUUID().toString())
                    .build()));
            Log.debugf("✅ Successfully published %s to Kafka", log.getClass().getSimpleName());
        } catch (Exception e) {
            Log.errorf(e, "❌ Failed to publish %s to Kafka: %s", log.getClass().getSimpleName(), e.getMessage());
        }
    }

    private String getStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement el : ex.getStackTrace()) {
            sb.append(el.toString()).append("\n");
        }
        return sb.toString();
    }
}
"@
Set-Content "$moduleDir/src/main/java/infrastructure/logging/LoggingHelper.java" $loggingHelper -Encoding UTF8

# 8) DatabaseOperationLogger.java
$dbLogger = @"
package infrastructure.logging;

import io.quarkus.arc.Arc;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Hibernate interceptor for automatic database operation logging
 * Tracks INSERT, UPDATE, DELETE operations with timing
 */
@ApplicationScoped
public class DatabaseOperationLogger {
    
    /**
     * Log database operation with automatic timing
     */
    public static <T> Uni<T> logOperation(String operation, String entityName, Uni<T> uniOperation) {
        long start = System.currentTimeMillis();
        
        return uniOperation
            .onItem().invoke(result -> {
                long duration = System.currentTimeMillis() - start;
                
                // Get LoggingHelper from CDI
                LoggingHelper logger = Arc.container().instance(LoggingHelper.class).get();
                
                // Always log performance metrics
                logger.logPerf(
                    "DB:" + operation + ":" + entityName, 
                    duration, 
                    duration > 100  // Mark as slow if > 100ms
                );
                
                if (duration > 100) {
                    Log.warnf("🐌 Slow DB operation: %s %s took %dms (sent to Kafka)", 
                        operation, entityName, duration);
                } else {
                    Log.debugf("⚡ DB operation: %s %s took %dms (sent to Kafka)", 
                        operation, entityName, duration);
                }
            })
            .onFailure().invoke(error -> {
                long duration = System.currentTimeMillis() - start;
                LoggingHelper logger = Arc.container().instance(LoggingHelper.class).get();
                
                // Log database errors
                logger.logError(error, null, null);
                Log.errorf(error, "❌ DB operation failed: %s %s after %dms", 
                    operation, entityName, duration);
            });
    }
    
    /**
     * Wrap persist operation with automatic logging
     */
    public static <T> Uni<T> logPersist(T entity, Uni<T> persistOp) {
        return logOperation("INSERT", entity.getClass().getSimpleName(), persistOp);
    }
    
    /**
     * Wrap update operation with automatic logging
     */
    public static <T> Uni<T> logUpdate(T entity, Uni<T> updateOp) {
        return logOperation("UPDATE", entity.getClass().getSimpleName(), updateOp);
    }
    
    /**
     * Wrap delete operation with automatic logging
     */
    public static <T> Uni<T> logDelete(String entityName, Uni<T> deleteOp) {
        return logOperation("DELETE", entityName, deleteOp);
    }
}
"@
Set-Content "$moduleDir/src/main/java/infrastructure/logging/DatabaseOperationLogger.java" $dbLogger -Encoding UTF8

# 9) AccessLogFilter.java
$accessFilter = @"
package infrastructure.logging;

import io.quarkus.arc.Arc;
import io.quarkus.logging.Log;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * JAX-RS filter for automatic HTTP access logging
 * Logs all incoming requests and outgoing responses
 */
@Provider
public class AccessLogFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String START_TIME_PROPERTY = "request.start.time";
    private static final String REQUEST_ID_PROPERTY = "request.id";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Store start time for response time calculation
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());
        
        // Generate request ID
        String requestId = java.util.UUID.randomUUID().toString();
        requestContext.setProperty(REQUEST_ID_PROPERTY, requestId);
        
        Log.debugf("📨 Incoming: %s %s [%s]", 
            requestContext.getMethod(), 
            requestContext.getUriInfo().getPath(),
            requestId);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, 
                      ContainerResponseContext responseContext) throws IOException {
        
        Long startTime = (Long) requestContext.getProperty(START_TIME_PROPERTY);
        if (startTime == null) {
            return;
        }
        
        long duration = System.currentTimeMillis() - startTime;
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        int status = responseContext.getStatus();
        
        // Get user info from headers (if authenticated)
        String userId = requestContext.getHeaderString("X-User-Id");
        
        // Log access
        try {
            LoggingHelper logger = Arc.container().instance(LoggingHelper.class).get();
            logger.logAccess(method, path, status, duration, userId);
            Log.debugf("📤 Sent AccessLog to Kafka: %s %s - %d", method, path, status);
        } catch (Exception e) {
            Log.errorf(e, "❌ Failed to log access: %s", e.getMessage());
        }
        
        // Console log with emoji based on status
        String emoji = getStatusEmoji(status);
        if (duration > 1000) {
            Log.warnf("%s %s %s - %d (%dms) 🐌 SLOW", 
                emoji, method, path, status, duration);
        } else {
            Log.infof("%s %s %s - %d (%dms)", 
                emoji, method, path, status, duration);
        }
    }
    
    private String getStatusEmoji(int status) {
        if (status >= 200 && status < 300) return "✅";
        if (status >= 300 && status < 400) return "🔄";
        if (status >= 400 && status < 500) return "⚠️";
        if (status >= 500) return "❌";
        return "❓";
    }
}
"@
Set-Content "$moduleDir/src/main/java/infrastructure/logging/AccessLogFilter.java" $accessFilter -Encoding UTF8

# 10) GlobalExceptionLogger.java
$exceptionLogger = @"
package infrastructure.logging;

import io.quarkus.arc.Arc;
import io.quarkus.logging.Log;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;

/**
 * Global exception handler with automatic error logging
 * Catches all uncaught exceptions and logs them automatically
 */
@Provider
public class GlobalExceptionLogger implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        // Log error automatically
        try {
            LoggingHelper logger = Arc.container().instance(LoggingHelper.class).get();
            if (logger != null) {
                logger.logError(exception, null, null);
            }
        } catch (Exception e) {
            Log.debugf("Could not log exception (LoggingHelper not ready): %s", e.getMessage());
        }
        
        // Log to console
        Log.errorf(exception, "❌ Unhandled exception: %s", exception.getMessage());
        
        // Return appropriate HTTP response
        int status = determineStatusCode(exception);
        return Response.status(status)
            .entity(new ErrorResponse(
                exception.getClass().getSimpleName(),
                exception.getMessage()
            ))
            .build();
    }
    
    private int determineStatusCode(Exception exception) {
        String exName = exception.getClass().getSimpleName().toLowerCase();
        
        if (exName.contains("notfound")) return 404;
        if (exName.contains("unauthorized") || exName.contains("authentication")) return 401;
        if (exName.contains("forbidden") || exName.contains("authorization")) return 403;
        if (exName.contains("validation") || exName.contains("illegal")) return 400;
        if (exName.contains("conflict")) return 409;
        
        return 500;
    }
    
    /**
     * Simple error response DTO
     */
    public static class ErrorResponse {
        public String error;
        public String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}
"@
Set-Content "$moduleDir/src/main/java/infrastructure/logging/GlobalExceptionLogger.java" $exceptionLogger -Encoding UTF8

# 11) auto-add to settings.gradle.kts
$settingsFile = Join-Path $root "settings.gradle.kts"
$includeLine = "include("":services:$moduleName"")"

if ((Get-Content $settingsFile) -notcontains $includeLine) {
    Add-Content $settingsFile $includeLine
}

Write-Host "`n✅ Module '$moduleName' CREATED SUCCESSFULLY!`n"
Write-Host "📦 Structure: Clean Architecture (4 layers)"
Write-Host "📁 Folders: application (dto/mapper/port/service/usecase), domain (entity/enums/event), infrastructure (logging/messaging/persistence/web), presentation (rest)"
Write-Host "🔧 Logging: LoggingHelper, DatabaseOperationLogger, AccessLogFilter, GlobalExceptionLogger"
Write-Host "🗄️  Database: PostgreSQL with Hibernate Reactive Panache"
Write-Host "📨 Kafka: 6 outgoing channels (audit-crud, audit-error, logs-*)"
Write-Host "🚀 Ready: Build with './gradlew :services:$moduleName:build'`n"
