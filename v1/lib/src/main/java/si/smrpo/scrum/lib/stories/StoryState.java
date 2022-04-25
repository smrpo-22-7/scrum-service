package si.smrpo.scrum.lib.stories;

import si.smrpo.scrum.lib.enums.StoryStatus;

public class StoryState {
    
    private String id;
    
    private boolean inActiveSprint;
    
    private StoryStatus storyStatus;
    
    private boolean estimated;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public boolean isInActiveSprint() {
        return inActiveSprint;
    }
    
    public void setInActiveSprint(boolean inActiveSprint) {
        this.inActiveSprint = inActiveSprint;
    }
    
    
    public boolean isEstimated() {
        return estimated;
    }
    
    public void setEstimated(boolean estimated) {
        this.estimated = estimated;
    }
    
    public StoryStatus getStoryStatus() {
        return storyStatus;
    }
    
    public void setStoryStatus(StoryStatus storyStatus) {
        this.storyStatus = storyStatus;
    }
}
