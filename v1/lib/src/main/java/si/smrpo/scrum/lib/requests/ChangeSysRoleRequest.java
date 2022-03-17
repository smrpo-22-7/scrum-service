package si.smrpo.scrum.lib.requests;

import java.util.Set;

public class ChangeSysRoleRequest {
    
    private Set<String> roles;
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
