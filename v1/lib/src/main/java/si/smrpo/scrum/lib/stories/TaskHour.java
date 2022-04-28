package si.smrpo.scrum.lib.stories;

import si.smrpo.scrum.lib.BaseType;

import java.time.Instant;

public class TaskHour extends BaseType {
    
    private Instant startDate;
    
    private Instant endDate;
    
    private Double amount;
    
    private String userId;
    
    private String taskId;
    
    private String taskName;
    
    private int storyNumberId;
    
    private String storyId;
    
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
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public int getStoryNumberId() {
        return storyNumberId;
    }
    
    public void setStoryNumberId(int storyNumberId) {
        this.storyNumberId = storyNumberId;
    }
    
    public String getStoryId() {
        return storyId;
    }
    
    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }
}
