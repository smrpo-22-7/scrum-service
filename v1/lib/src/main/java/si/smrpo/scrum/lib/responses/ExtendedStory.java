package si.smrpo.scrum.lib.responses;

import si.smrpo.scrum.lib.stories.Story;

public class ExtendedStory extends Story {
    
    private boolean inActiveSprint;
    
    private String assignedSprintId;
    
    public ExtendedStory() {
    
    }
    
    public ExtendedStory(Story story) {
        this.id = story.getId();
        this.createdAt = story.getCreatedAt();
        this.updatedAt = story.getUpdatedAt();
        this.status = story.getStatus();
        this.description = story.getDescription();
        this.businessValue = story.getBusinessValue();
        this.priority = story.getPriority();
        this.title = story.getTitle();
        this.numberId = story.getNumberId();
        this.timeEstimate = story.getTimeEstimate();
        if (story.isRealized() != null) {
            this.realized = story.isRealized();
        }
        if (story.getTests() != null) {
            this.tests = story.getTests();
        }
        if (story.getProjectId() != null) {
            this.projectId = story.getProjectId();
        }
    }
    
    public boolean isInActiveSprint() {
        return inActiveSprint;
    }
    
    public void setInActiveSprint(boolean inActiveSprint) {
        this.inActiveSprint = inActiveSprint;
    }
    
    public String getAssignedSprintId() {
        return assignedSprintId;
    }
    
    public void setAssignedSprintId(String assignedSprintId) {
        this.assignedSprintId = assignedSprintId;
    }
}
