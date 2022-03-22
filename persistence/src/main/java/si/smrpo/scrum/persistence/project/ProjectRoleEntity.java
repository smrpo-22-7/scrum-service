package si.smrpo.scrum.persistence.project;

import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "project_roles", indexes = {
        @Index(name = "UNIQUE_PROJECT_ROLE_ID", columnList = "role_id", unique = true)
})
@NamedQueries({
        @NamedQuery(name = ProjectRoleEntity.GET_BY_ROLE_ID, query = "SELECT r FROM ProjectRoleEntity r WHERE r.roleId = :roleId")
})
public class ProjectRoleEntity extends BaseEntity {

    public static final String GET_BY_ROLE_ID = "ProjectRoleEntity.getByRoleId";
    
    public static final String PROJECT_ROLE_MEMBER = "member";
    public static final String PROJECT_ROLE_PRODUCT_OWNER = "product_owner";
    public static final String PROJECT_ROLE_SCRUM_MASTER = "scrum_master";

    @Column(name = "role_id", unique = true, nullable = false)
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
