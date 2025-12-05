package infrastructure.persistence;

import io.quarkus.arc.Unremovable;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
@Unremovable
public class UserContext {

    private String userId = "system";
    private String username;
    private String ipAddress;

    public String getCurrentUserId() {
        return userId != null ? userId : "system";
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setCurrentUser(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public void setCurrentUser(String userId, String username, String ipAddress) {
        this.userId = userId;
        this.username = username;
        this.ipAddress = ipAddress;
    }

    public void clear() {
        this.userId = "system";
        this.username = null;
        this.ipAddress = null;
    }
}
