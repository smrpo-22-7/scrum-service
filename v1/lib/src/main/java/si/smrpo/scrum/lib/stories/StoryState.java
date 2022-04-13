package si.smrpo.scrum.lib.stories;

public class StoryState {
    
    private String id;
    
    private boolean inActiveSprint;
    
    private boolean realized;
    
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
    
    public boolean isRealized() {
        return realized;
    }
    
    public void setRealized(boolean realized) {
        this.realized = realized;
    }
    
    public boolean isEstimated() {
        return estimated;
    }
    
    public void setEstimated(boolean estimated) {
        this.estimated = estimated;
    }
}
