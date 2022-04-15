package si.smrpo.scrum.lib.responses;

import java.time.Instant;

public class ProjectSprintStatus {
    
    private boolean active;
    
    private Instant startDate;
    
    private Instant endDate;
    
    private Integer expectedSpeed;
    
    private String projectId;
    
    private String sprintId;
    
    private Long assignedPoints;
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
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
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public Long getAssignedPoints() {
        return assignedPoints;
    }
    
    public void setAssignedPoints(Long assignedPoints) {
        this.assignedPoints = assignedPoints;
    }
    
    public String getSprintId() {
        return sprintId;
    }
    
    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }
}
