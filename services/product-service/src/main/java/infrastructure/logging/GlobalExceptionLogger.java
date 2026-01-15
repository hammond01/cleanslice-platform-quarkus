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
    
    public static class ErrorResponse {
        public String error;
        public String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}
