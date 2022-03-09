package si.smrpo.scrum.persistence.identifiers;

import si.smrpo.scrum.persistence.users.SysRoleEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserRoleId implements Serializable {
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne
    @JoinColumn(name = "sys_role_id", nullable = false)
    private SysRoleEntity sysRole;
    
    public UserEntity getUser() {
        return user;
    }
    
    public void setUser(UserEntity user) {
        this.user = user;
    }
    
    public SysRoleEntity getSysRole() {
        return sysRole;
    }
    
    public void setSysRole(SysRoleEntity sysRole) {
        this.sysRole = sysRole;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoleId that = (UserRoleId) o;
        return user.getId().equals(that.user.getId()) && sysRole.getId().equals(that.sysRole.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), sysRole.getId());
    }
}
