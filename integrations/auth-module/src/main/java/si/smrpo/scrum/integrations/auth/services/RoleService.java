package si.smrpo.scrum.integrations.auth.services;

import si.smrpo.scrum.lib.SysRole;
import si.smrpo.scrum.persistence.users.SysRoleEntity;

import java.util.Optional;
import java.util.Set;

public interface RoleService {
    
    Set<String> getUserRoles(String userId);
    
    Set<SysRoleEntity> getUserRoleEntities(String userId);
    
    Set<SysRoleEntity> getSysRoles(Set<String> roles);
    
    Set<SysRoleEntity> getAllSysRoleEntities();
    
    Set<SysRole> getAllSysRoles();
    
    Optional<SysRoleEntity> getSysRoleEntity(String roleId);
    
}
