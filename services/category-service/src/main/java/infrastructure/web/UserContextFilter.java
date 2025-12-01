package infrastructure.web;

import infrastructure.persistence.UserContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserContextFilter implements ContainerRequestFilter {

    @Inject
    UserContext userContext;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String userId = requestContext.getHeaderString("X-User-Id");
        String username = requestContext.getHeaderString("X-Username");
        String ipAddress = requestContext.getHeaderString("X-Forwarded-For");
        
        if (ipAddress == null) {
            ipAddress = requestContext.getHeaderString("X-Real-IP");
        }

        if (userId != null && username != null) {
            userContext.setCurrentUser(userId, username, ipAddress);
        } else if (userId != null) {
            userContext.setCurrentUser(userId, userId, ipAddress);
        } else {
            userContext.setCurrentUser("system", "system", ipAddress);
        }
    }
}
