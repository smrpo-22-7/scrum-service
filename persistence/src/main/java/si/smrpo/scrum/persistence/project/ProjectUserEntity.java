package si.smrpo.scrum.persistence.project;


import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.persistence.identifiers.ProjectUserId;


import javax.persistence.*;


@Entity
@Table(name = "project_users")
@NamedQueries({
    @NamedQuery(name = ProjectUserEntity.DELETE_BY_USER_AND_PROJECT, query = "DELETE FROM ProjectUserEntity pu WHERE pu.id.project.id = :projectId AND pu.id.user.id = :userId"),
    @NamedQuery(name = ProjectUserEntity.GET_BY_USER_AND_PROJECT, query = "SELECT pu FROM ProjectUserEntity pu WHERE pu.id.project.id = :projectId AND pu.id.user.id = :userId"),
    @NamedQuery(name = ProjectUserEntity.GET_BY_PROJECT, query = "SELECT pu FROM ProjectUserEntity pu WHERE pu.id.project.id = :projectId"),
    // @formatter:off
    @NamedQuery(name = ProjectUserEntity.GET_ROLES_COUNT,
        query = "SELECT new si.smrpo.scrum.persistence.aggregators.ProjectMembersAggregated(" +
                "SUM (CASE WHEN pr.roleId = 'member' THEN 1 ELSE 0 END), " +
                "SUM (CASE WHEN pr.roleId = 'scrum_master' THEN 1 ELSE 0 END), " +
                "SUM (CASE WHEN pr.roleId = 'product_owner' THEN 1 ELSE 0 END) " +
            ") FROM ProjectUserEntity pu " +
            "JOIN ProjectRoleEntity pr ON pu.projectRole.id = pr.id " +
            "WHERE pu.id.project.id = :projectId"),
    @NamedQuery(name = ProjectUserEntity.GET_PROJECT_MEMBERS,
        query = "SELECT pu " +
            "FROM ProjectUserEntity pu " +
            "WHERE pu.id.project.id = :projectId " +
            "ORDER BY CASE pu.projectRole.roleId " +
                "WHEN 'member' THEN 3 " +
                "WHEN 'scrum_master' THEN 2 " +
                "WHEN 'product_owner' THEN 1 END ASC"),
    @NamedQuery(name = ProjectUserEntity.COUNT_PROJECT_MEMBERS,
        query = "SELECT COUNT(pu) FROM ProjectUserEntity pu " +
                "WHERE pu.id.project.id = :projectId ")
        // @formatter:on
})
public class ProjectUserEntity {
    
    public static final String GET_BY_PROJECT = "ProjectUserEntity.getByProject";
    public static final String DELETE_BY_USER_AND_PROJECT = "ProjectUserEntity.deleteByUserAndProject";
    public static final String GET_BY_USER_AND_PROJECT = "ProjectUserEntity.getByUserAndProject";
    public static final String GET_ROLES_COUNT = "ProjectUserEntity.getRolesCount";
    public static final String GET_PROJECT_MEMBERS = "ProjectUserEntity.getProjectMembers";
    public static final String COUNT_PROJECT_MEMBERS = "ProjectUserEntity.countProjectMembers";
    
    @EmbeddedId
    private ProjectUserId id;
    
    @ManyToOne
    @JoinColumn(name = "project_role_id", nullable = false)
    private ProjectRoleEntity projectRole;
    
    public void setId(ProjectUserId id) {
        this.id = id;
    }
    
    public ProjectUserId getId() {
        return id;
    }
    
    public UserEntity getUser() {
        return this.id.getUser();
    }
    
    public ProjectEntity getProject() {
        return this.id.getProject();
    }
    
    public ProjectRoleEntity getProjectRole() {
        return projectRole;
    }
    
    public void setProjectRole(ProjectRoleEntity projectRole) {
        this.projectRole = projectRole;
    }
}
