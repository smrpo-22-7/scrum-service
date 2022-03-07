package si.smrpo.scrum.integrations.auth.services;

import java.util.Set;

public interface RoleService {
    
    Set<String> getUserRoles(String userId);
    
}
