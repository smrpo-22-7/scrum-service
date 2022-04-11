package si.smrpo.scrum.lib.stories;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.StoryPriority;

import java.util.List;

public class Story extends BaseType {

    private String title;

    private String description;
    
    private Integer numberId;

    private SimpleStatus status;

    private Integer businessValue;
    
    private Integer timeEstimate;

    private StoryPriority priority;

    private Boolean realized;
    
    private Boolean assigned;

    private List<AcceptanceTest> tests;
    
    private String projectId;

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

    public Boolean isRealized() {return realized;}

    public void setRealized(Boolean realized) {this.realized = realized;}
    
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
}
