package si.smrpo.scrum.persistence.story;

import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.StoryPriority;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.project.ProjectEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "stories", indexes = {
    @Index(name = "INDEX_STORIES_PROJECT_ID", columnList = "project_id")
})
public class StoryEntity extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;

    @Column(name = "business_value")
    private int businessValue;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private StoryPriority priority;
    
    @Column(name = "time_estimate")
    private int timeEstimate;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;
    
    @OneToMany(mappedBy = "story")
    private List<AcceptanceTestEntity> tests;

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

    public int getBusinessValue() {
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

    public List<AcceptanceTestEntity> getTests() {
        return tests;
    }

    public void setTests(List<AcceptanceTestEntity> tests) {
        this.tests = tests;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }
    
    public int getTimeEstimate() {
        return timeEstimate;
    }
    
    public void setTimeEstimate(int timeEstimate) {
        this.timeEstimate = timeEstimate;
    }
}

