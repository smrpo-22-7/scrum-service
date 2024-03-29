package si.smrpo.scrum.lib.stories;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.StoryPriority;
import si.smrpo.scrum.lib.enums.StoryStatus;

import java.util.List;

public class Story extends BaseType {

    protected String title;
    
    protected String description;
    
    protected Integer numberId;
    
    protected SimpleStatus status;
    
    protected Integer businessValue;
    
    protected Integer timeEstimate;
    
    protected StoryPriority priority;
    
    protected StoryStatus storyStatus;
    
    protected String rejectComment;
    
    protected Boolean assigned;
    
    protected List<AcceptanceTest> tests;
    
    protected String projectId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SimpleStatus getStatus() {
        return status;
    }

    public void setStatus(SimpleStatus status) {
        this.status = status;
    }

    public Integer getBusinessValue() {
        return businessValue;
    }

    public void setBusinessValue(int businessValue) {
        this.businessValue = businessValue;
    }

    public StoryPriority getPriority() {
        return priority;
    }

    public void setPriority(StoryPriority priority) {
        this.priority = priority;
    }

    public List<AcceptanceTest> getTests() {
        return tests;
    }

    public void setTests(List<AcceptanceTest> tests) {
        this.tests = tests;
    }
    
    public void setBusinessValue(Integer businessValue) {
        this.businessValue = businessValue;
    }
    
    public Integer getTimeEstimate() {
        return timeEstimate;
    }
    
    public void setTimeEstimate(Integer timeEstimate) {
        this.timeEstimate = timeEstimate;
    }
    
    public Integer getNumberId() {
        return numberId;
    }
    
    public void setNumberId(Integer numberId) {
        this.numberId = numberId;
    }

    
    public Boolean getAssigned() {
        return assigned;
    }
    
    public void setAssigned(Boolean assigned) {
        this.assigned = assigned;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getRejectComment() {
        return rejectComment;
    }
    
    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment;
    }
    
    public StoryStatus getStoryStatus() {
        return storyStatus;
    }
    
    public void setStoryStatus(StoryStatus storyStatus) {
        this.storyStatus = storyStatus;
    }
}
