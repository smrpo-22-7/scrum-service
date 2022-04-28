package si.smrpo.scrum.lib.stories;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.UserProfile;

import java.time.Instant;

public class TaskWorkSpent extends BaseType {
    
    private Instant workDate;
    
    private Double amount;
    
    private Double remainingAmount;
    
    private UserProfile user;
    
    private String userId;
    
    private TaskWorkSpent.ProjectTask task;
    
    public Instant getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(Instant workDate) {
        this.workDate = workDate;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
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
    
    public ProjectTask getTask() {
        return task;
    }
    
    public void setTask(ProjectTask task) {
        this.task = task;
    }
    
    public Double getRemainingAmount() {
        return remainingAmount;
    }
    
    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    
    public static class ProjectTask {
        private String taskId;
        
        private String taskDescription;
        
        private String storyId;
        
        private int storyNumberId;
        
        private String projectId;
        
        private String projectTitle;
        
        private boolean completed;
        
        public ProjectTask() {
        
        }
        
        public ProjectTask(String id, String description,
                           String projectId, String projectTitle,
                           String storyId, int storyNumberId, boolean completed) {
            this.taskId = id;
            this.taskDescription = description;
            this.projectId = projectId;
            this.projectTitle = projectTitle;
            this.storyId = storyId;
            this.storyNumberId = storyNumberId;
            this.completed = completed;
        }
    
        public String getStoryId() {
            return storyId;
        }
    
        public void setStoryId(String storyId) {
            this.storyId = storyId;
        }
    
        public int getStoryNumberId() {
            return storyNumberId;
        }
    
        public void setStoryNumberId(int storyNumberId) {
            this.storyNumberId = storyNumberId;
        }
    
        public String getTaskId() {
            return taskId;
        }
        
        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }
        
        public String getTaskDescription() {
            return taskDescription;
        }
        
        public void setTaskDescription(String taskDescription) {
            this.taskDescription = taskDescription;
        }
    
        public String getProjectId() {
            return projectId;
        }
    
        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }
    
        public String getProjectTitle() {
            return projectTitle;
        }
    
        public void setProjectTitle(String projectTitle) {
            this.projectTitle = projectTitle;
        }
    
        public boolean isCompleted() {
            return completed;
        }
    
        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}
