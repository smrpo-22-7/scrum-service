package si.smrpo.scrum.lib.requests;

import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.StoryPriority;
import si.smrpo.scrum.lib.stories.AcceptanceTest;

import java.util.List;

public class CreateStoryRequest {

    private String title;

    private String description;

    private SimpleStatus status;

    private Integer businessValue;
    
    protected Integer timeEstimate;

    private StoryPriority priority;

    private List<AcceptanceTest> tests;

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

    public void setBusinessValue(Integer businessValue) {
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
    
    public Integer getTimeEstimate() {
        return timeEstimate;
    }
    
    public void setTimeEstimate(Integer timeEstimate) {
        this.timeEstimate = timeEstimate;
    }
}
