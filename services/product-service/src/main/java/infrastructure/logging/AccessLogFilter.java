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
        // String username = requestContext.getHeaderString("X-Username");
        
        // Log access
        try {
            LoggingHelper logger = Arc.container().instance(LoggingHelper.class).get();
            if (logger != null) {
                logger.logAccess(method, path, status, duration, userId);
            }
        } catch (Exception e) {
            Log.debugf("Could not log access (LoggingHelper not ready): %s", e.getMessage());
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
        return "ℹ️";
    }
}
