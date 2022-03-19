package si.smrpo.scrum.lib.projects;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.enums.SimpleStatus;

public class Project extends BaseType {

    private String name;

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
