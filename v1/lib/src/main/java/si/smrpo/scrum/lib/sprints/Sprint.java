package si.smrpo.scrum.lib.sprints;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.enums.SimpleStatus;

import java.util.Date;

public class Sprint extends BaseType {

    private String title;

    private Date startDate;

    private Date endDate;

    private SimpleStatus status;

    private Integer expectedSpeed;

    private String projectId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getExpectedSpeed() {
        return expectedSpeed;
    }

    public void setExpectedSpeed(Integer expectedSpeed) {
        this.expectedSpeed = expectedSpeed;
    }

    public SimpleStatus getStatus() {
        return status;
    }

    public void setStatus(SimpleStatus status) {
        this.status = status;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
