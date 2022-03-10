package si.smrpo.scrum.persistence.users;

import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "sys_roles", indexes = {
    @Index(name = "IDX_UNIQUE_ROLE_ID", columnList = "role_id", unique = true)
})
@NamedQueries({
    @NamedQuery(name = SysRoleEntity.GET_BY_ROLE_ID, query = "SELECT r FROM SysRoleEntity r WHERE r.roleId = :roleId"),
    @NamedQuery(name = SysRoleEntity.GET_BY_ROLE_IDS, query = "SELECT r FROM SysRoleEntity r WHERE r.roleId IN :roleIds")
})
public class SysRoleEntity extends BaseEntity {
    
    public static final String GET_BY_ROLE_ID = "SysRoleEntity.getByRoleId";
    public static final String GET_BY_ROLE_IDS = "SysRoleEntity.getByRoleIds";
    
    @Column(name = "role_id", nullable = false)
    private String roleId;
    
    @Column(name = "name")
    private String name;
    
    public String getRoleId() {
        return roleId;
    }
    
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
