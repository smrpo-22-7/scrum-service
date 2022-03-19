package si.smrpo.scrum.persistence.identifiers;

import si.smrpo.scrum.persistence.project.ProjectEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProjectUserId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectUserId that = (ProjectUserId) o;
        return user.getId().equals(that.getUser().getId()) && project.getId().equals(that.getProject().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), project.getId());
    }
}
