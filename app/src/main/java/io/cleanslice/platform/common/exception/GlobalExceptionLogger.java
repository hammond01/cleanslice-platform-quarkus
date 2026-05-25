package io.cleanslice.platform.common.exception;

import io.quarkus.arc.Arc;
import io.cleanslice.platform.common.logging.LoggingHelper;
import io.quarkus.logging.Log;

/**
 * Utility for automatic error logging.
 * Used by GlobalExceptionHandler to log unhandled exceptions to the structured log store.
 */
public class GlobalExceptionLogger {

    /**
     * Log an exception to the structured error log store via LoggingHelper.
     * Fails silently if LoggingHelper is not available (e.g. during startup).
     */
    public static void logException(Exception exception, String userId, String correlationId) {
        try {
            LoggingHelper logger = Arc.container().instance(LoggingHelper.class).get();
            if (logger != null) {
                logger.logError(exception, userId, correlationId);
            }
        } catch (Exception e) {
            Log.debugf("Could not log exception (LoggingHelper not ready): %s", e.getMessage());
        }

        Log.errorf(exception, "❌ Unhandled exception: %s", exception.getMessage());
    }
}


