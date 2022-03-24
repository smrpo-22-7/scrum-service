package si.smrpo.scrum.integrations.auth.mappers;

import si.smrpo.scrum.lib.SysRole;
import si.smrpo.scrum.persistence.users.SysRoleEntity;

public class RoleMapper {
    
    private RoleMapper() {
    
    }
    
    public static SysRole fromEntity(SysRoleEntity entity) {
        SysRole role = new SysRole();
        role.setId(entity.getId());
        if (entity.getCreatedAt() != null) {
            role.setCreatedAt(entity.getCreatedAt().toInstant());
        }
        if (entity.getUpdatedAt() != null) {
            role.setUpdatedAt(entity.getUpdatedAt().toInstant());
        }
        role.setRoleId(entity.getRoleId());
        role.setName(entity.getName());
        return role;
    }
    
}
