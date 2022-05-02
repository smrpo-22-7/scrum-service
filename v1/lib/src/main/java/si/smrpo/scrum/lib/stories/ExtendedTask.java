package si.smrpo.scrum.lib.stories;

public class ExtendedTask extends Task {
    
    private boolean isActive;
    
    private Story story;
    
    private double amountWorked;
    private double amountRemaining;
    
    public ExtendedTask() {
    
    }
    
    public ExtendedTask(Task task) {
        this.id = task.getId();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.completed = task.getCompleted();
        this.description = task.getDescription();
        this.estimate = task.getEstimate();
        this.status = task.getStatus();
        this.storyId = task.getStoryId();
        this.assignment = task.getAssignment();
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public Story getStory() {
        return story;
    }
    
    public void setStory(Story story) {
        this.story = story;
    }
    
    public double getAmountWorked() {
        return amountWorked;
    }
    
    public void setAmountWorked(double amountWorked) {
        this.amountWorked = amountWorked;
    }
    
    public double getAmountRemaining() {
        return amountRemaining;
    }
    
    public void setAmountRemaining(double amountRemaining) {
        this.amountRemaining = amountRemaining;
    }
}
