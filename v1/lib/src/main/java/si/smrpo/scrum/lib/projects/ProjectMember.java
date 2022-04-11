package si.smrpo.scrum.lib.projects;

import si.smrpo.scrum.lib.UserProfile;

public class ProjectMember {

    private String projectId;

    private UserProfile user;

    private String userId;

    private String projectRoleId;

    private ProjectRole projectRole;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
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
