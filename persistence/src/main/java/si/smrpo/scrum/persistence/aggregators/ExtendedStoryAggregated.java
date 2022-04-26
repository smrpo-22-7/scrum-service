package si.smrpo.scrum.persistence.aggregators;

import si.smrpo.scrum.persistence.story.StoryEntity;

import java.util.Objects;

public class ExtendedStoryAggregated {
    
    private StoryEntity story;
    
    private boolean isAssigned;
    
    private String assignedTo;
    
    private Long totalTasks;
    
    private Long completedTasks;
    
    public ExtendedStoryAggregated() {
    
    }
    
    public ExtendedStoryAggregated(
        StoryEntity story,
        boolean isAssigned,
        String assignedTo,
        Long totalTasks,
        Long completedTasks
    ) {
        this.story = story;
        this.isAssigned = isAssigned;
        this.assignedTo = assignedTo;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
    }
    
    public StoryEntity getStory() {
        return story;
    }
    
    public void setStory(StoryEntity story) {
        this.story = story;
    }
    
    public boolean isAssigned() {
        return isAssigned;
    }
    
    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }
    
    public Long getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(Long totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public Long getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(Long completedTasks) {
        this.completedTasks = completedTasks;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtendedStoryAggregated that = (ExtendedStoryAggregated) o;
        return story.getId().equals(that.story.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(story.getId());
    }
}
