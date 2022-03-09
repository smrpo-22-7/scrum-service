package si.smrpo.scrum.persistence.users;

import si.smrpo.scrum.persistence.identifiers.UserRoleId;

import javax.persistence.*;

@Entity
@Table(name = "user_sys_roles")
@NamedQueries({
    @NamedQuery(name = UserSysRolesEntity.GET_USER_ROLES, query = "SELECT ur.id.sysRole FROM UserSysRolesEntity ur WHERE ur.id.user.id = :userId")
})
public class UserSysRolesEntity {
    
    public static final String GET_USER_ROLES = "UserSysRolesEntity.getUserRoles";
    
    @EmbeddedId
    private UserRoleId id;
    
    public UserRoleId getId() {
        return id;
    }
    
    public void setId(UserRoleId id) {
        this.id = id;
    }
    
    public UserEntity getUser() {
        return this.id.getUser();
    }
    
    public SysRoleEntity getSysRole() {
        return this.id.getSysRole();
    }
}
