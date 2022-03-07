package si.smrpo.scrum.integrations.auth.services.impl;

import si.smrpo.scrum.integrations.auth.services.RoleService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Set;

@RequestScoped
public class RoleServiceImpl implements RoleService {
    
    @Inject
    private EntityManager em;
    
    @Override
    public Set<String> getUserRoles(String userId) {
        return new HashSet<>();
    }
}
