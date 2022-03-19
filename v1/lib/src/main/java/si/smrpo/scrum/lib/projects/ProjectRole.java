package si.smrpo.scrum.lib.projects;

import si.smrpo.scrum.lib.BaseType;

public class ProjectRole extends BaseType {

    private String roleId;

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
