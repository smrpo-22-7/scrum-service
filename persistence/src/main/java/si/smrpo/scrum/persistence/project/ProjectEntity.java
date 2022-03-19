package si.smrpo.scrum.persistence.project;

import liquibase.pro.packaged.P;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "projects", indexes = {
        @Index(name = "UNIQUE_PROJECT_NAME", columnList = "name", unique = true)
})
@NamedQueries({
        @NamedQuery(name = ProjectEntity.GET_BY_PROJECT_NAME, query = "SELECT r FROM ProjectEntity r WHERE LOWER(r.name) = LOWER(:name)"),
        @NamedQuery(name = ProjectEntity.GET_USER_PROJECTS, query = "SELECT pu.id.project FROM ProjectUserEntity pu WHERE pu.id.user.id = :userId AND pu.id.project.status = 'ACTIVE'"),
        @NamedQuery(name = ProjectEntity.COUNT_USER_PROJECT, query = "SELECT COUNT(pu) FROM ProjectUserEntity pu WHERE pu.id.user.id = :userId AND pu.id.project.status = 'ACTIVE'")
})
public class ProjectEntity extends BaseEntity {

    public static final String GET_BY_PROJECT_NAME = "ProjectEntity.getByName";
    public static final String GET_USER_PROJECTS = "ProjectEntity.getUserProjects";
    public static final String COUNT_USER_PROJECT = "ProjectEntity.countUserProject";

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleStatus getStatus() {
        return status;
    }

    public void setStatus(SimpleStatus status) {
        this.status = status;
    }
}
