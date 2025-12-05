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

    // private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();
    
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
                
                // Log if slow (> 100ms for DB operations)
                if (duration > 100) {
                    logger.logPerf(
                        "DB:" + operation + ":" + entityName, 
                        duration, 
                        true
                    );
                    Log.warnf("🐌 Slow DB operation: %s %s took %dms", 
                        operation, entityName, duration);
                } else {
                    Log.debugf("⚡ DB operation: %s %s took %dms", 
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
