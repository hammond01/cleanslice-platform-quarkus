package share.enums;

/**
 * Log severity levels following industry standards
 * Compatible with SLF4J, Log4j2, and other logging frameworks
 */
public enum LogLevel {
    TRACE,   // Finest-grained informational events
    DEBUG,   // Fine-grained informational events for debugging
    INFO,    // Informational messages highlighting progress
    WARN,    // Potentially harmful situations
    ERROR,   // Error events that might still allow app to continue
    FATAL    // Very severe error events that might cause app termination
}