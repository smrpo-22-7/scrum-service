package si.smrpo.scrum.persistence.aggregators;

import si.smrpo.scrum.persistence.story.StoryEntity;

public class ExtendedStoryAggregated {
    
    private StoryEntity story;
    
    private boolean isAssigned;
    
    private String assignedTo;
    
    public ExtendedStoryAggregated() {
    
    }
    
    public ExtendedStoryAggregated(StoryEntity story, boolean isAssigned, String assignedTo) {
        this.story = story;
        this.isAssigned = isAssigned;
        this.assignedTo = assignedTo;
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
}
