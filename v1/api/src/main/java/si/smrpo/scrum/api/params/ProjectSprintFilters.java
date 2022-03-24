package si.smrpo.scrum.api.params;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class ProjectSprintFilters {

    @QueryParam("active")
    @DefaultValue("true")
    private boolean active;
    
    @QueryParam("future")
    @DefaultValue("true")
    private boolean future;
    
    @QueryParam("past")
    @DefaultValue("true")
    private boolean past;
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isFuture() {
        return future;
    }
    
    public void setFuture(boolean future) {
        this.future = future;
    }
    
    public boolean isPast() {
        return past;
    }
    
    public void setPast(boolean past) {
        this.past = past;
    }
}
