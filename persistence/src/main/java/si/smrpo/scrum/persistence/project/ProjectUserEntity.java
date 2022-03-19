package si.smrpo.scrum.persistence.project;


import si.smrpo.scrum.persistence.users.UserEntity;
import si.smrpo.scrum.persistence.identifiers.ProjectUserId;


import javax.persistence.*;


@Entity
@Table(name = "project_users")
@NamedQueries({
        @NamedQuery(name = ProjectUserEntity.DELETE_BY_USER_AND_PROJECT, query = "DELETE FROM ProjectUserEntity pu WHERE pu.id.project.id = :projectId AND pu.id.user.id = :userId"),
        @NamedQuery(name = ProjectUserEntity.GET_BY_USER_AND_PROJECT, query = "SELECT pu FROM ProjectUserEntity pu WHERE pu.id.project.id = :projectId AND pu.id.user.id = :userId")
})
public class ProjectUserEntity {

    public static final String GET_BY_PROJECT = "ProjectUserEntity.getByProject";
    public static final String DELETE_BY_USER_AND_PROJECT = "ProjectUserEntity.deleteByUserAndProject";
    public static final String GET_BY_USER_AND_PROJECT = "ProjectUserEntity.getByUserAndProject";

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
