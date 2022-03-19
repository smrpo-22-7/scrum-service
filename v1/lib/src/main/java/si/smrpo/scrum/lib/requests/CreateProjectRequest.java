package si.smrpo.scrum.lib.requests;

import si.smrpo.scrum.lib.projects.ProjectMember;

import java.util.List;

public class CreateProjectRequest {

    private String name;

    private List<ProjectMember> members;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }
}
