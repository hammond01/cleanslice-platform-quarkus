package share.enums;

/**
 * Types of logs in the centralized logging system
 * Each type has specific schema and use case
 */
public enum LogType {
    AUDIT,          // Compliance, regulatory, user actions
    APPLICATION,    // Business logic, workflow, general app logs
    ERROR,          // Exceptions, errors, failures
    ACCESS,         // HTTP requests, API calls, gateway logs
    PERFORMANCE,    // Metrics, slow queries, response times
    SECURITY,       // Authentication, authorization, threats
    SYSTEM          // Infrastructure, health checks, startup/shutdown
}
