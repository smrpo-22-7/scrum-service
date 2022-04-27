package si.smrpo.scrum.lib.stories;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.enums.SimpleStatus;

public class Task extends BaseType {
    
    protected String description;
    
    protected Double estimate;
    
    protected Boolean completed;
    
    protected String storyId;
    
    protected TaskAssignment assignment;
    
    protected SimpleStatus status;
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getEstimate() {
        return estimate;
    }
    
    public void setEstimate(Double estimate) {
        this.estimate = estimate;
    }
    
    public Boolean getCompleted() {
        return completed;
    }
    
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    
    public String getStoryId() {
        return storyId;
    }
    
    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }
    
    public TaskAssignment getAssignment() {
        return assignment;
    }
    
    public void setAssignment(TaskAssignment assignment) {
        this.assignment = assignment;
    }
    
    public SimpleStatus getStatus() {
        return status;
    }
    
    public void setStatus(SimpleStatus status) {
        this.status = status;
    }
    
    public static class TaskAssignment {
        
        private boolean pending;
    
        private UserProfile assignee;
    
        private String assigneeId;
    
        public boolean isPending() {
            return pending;
        }
    
        public void setPending(boolean pending) {
            this.pending = pending;
        }
    
        public UserProfile getAssignee() {
            return assignee;
        }
    
        public void setAssignee(UserProfile assignee) {
            this.assignee = assignee;
        }
    
        public String getAssigneeId() {
            return assigneeId;
        }
    
        public void setAssigneeId(String assigneeId) {
            this.assigneeId = assigneeId;
        }
    }
}
