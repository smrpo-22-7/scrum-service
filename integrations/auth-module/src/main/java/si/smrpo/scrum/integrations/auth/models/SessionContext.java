package si.smrpo.scrum.integrations.auth.models;

public class SessionContext {
    
    private boolean active;
    
    private String sessionId;
    
    public SessionContext() {
        this.active = false;
        this.sessionId = null;
    }
    
    public SessionContext(String sessionId) {
        this.active = true;
        this.sessionId = sessionId;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
