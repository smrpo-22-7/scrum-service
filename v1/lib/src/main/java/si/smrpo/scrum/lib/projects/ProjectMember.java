package si.smrpo.scrum.lib.projects;

import si.smrpo.scrum.lib.User;

public class ProjectMember {

    private String projectId;

    private User user;

    private String userId;

    private String projectRoleId;

    private ProjectRole projectRole;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProjectRoleId() {
        return projectRoleId;
    }

    public void setProjectRoleId(String projectRoleId) {
        this.projectRoleId = projectRoleId;
    }

    public ProjectRole getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(ProjectRole projectRole) {
        this.projectRole = projectRole;
    }
}
