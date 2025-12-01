package infrastructure.persistence;

import domain.entity.BaseEntity;
import io.quarkus.arc.Arc;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * JPA Entity Listener for automatic auditing
 * Automatically sets createdBy, lastModifiedBy from UserContext
 */
@ApplicationScoped
public class AuditingEntityListener {

    @Inject
    UserContext userContext;

    @PrePersist
    public void setCreatedBy(BaseEntity entity) {
        String currentUser = getCurrentUser();
        if (entity.createdBy == null) {
            entity.createdBy = currentUser;
        }
    }

    @PreUpdate
    public void setLastModifiedBy(BaseEntity entity) {
        entity.lastModifiedBy = getCurrentUser();
    }

    private String getCurrentUser() {
        try {
            // Try to get from injected context
            if (userContext != null) {
                return userContext.getCurrentUserId();
            }
            
            // Fallback to Arc container lookup
            UserContext ctx = Arc.container().instance(UserContext.class).get();
            if (ctx != null) {
                return ctx.getCurrentUserId();
            }
        } catch (Exception e) {
            // Ignore and return default
        }
        
        return "system";
    }
}
