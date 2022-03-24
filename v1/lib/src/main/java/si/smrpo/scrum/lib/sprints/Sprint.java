package si.smrpo.scrum.lib.sprints;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.enums.SimpleStatus;

import java.time.Instant;

public class Sprint extends BaseType {

    private String title;

    private Instant startDate;

    private Instant endDate;

    private SimpleStatus status;

    private Integer expectedSpeed;

    private String projectId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
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
