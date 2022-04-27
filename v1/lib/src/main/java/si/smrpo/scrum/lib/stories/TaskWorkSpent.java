package si.smrpo.scrum.lib.stories;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.UserProfile;

import java.time.Instant;

public class TaskWorkSpent extends BaseType {
    
    private Instant workDate;
    
    private double amount;
    
    private UserProfile user;
    
    private String userId;
    
    private TaskWorkSpent.ProjectTask task;
    
    public Instant getWorkDate() {
        return workDate;
    }
    
    public void setWorkDate(Instant workDate) {
        this.workDate = workDate;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
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
    
    public static class ProjectTask {
        private String taskId;
        private String taskDescription;
        
        private String projectId;
        
        private String projectTitle;
        
        public ProjectTask() {
        
        }
        
        public ProjectTask(String id, String description, String projectId, String projectTitle) {
            this.taskId = id;
            this.taskDescription = description;
            this.projectId = projectId;
            this.projectTitle = projectTitle;
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
    }
}
