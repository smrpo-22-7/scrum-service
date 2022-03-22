package si.smrpo.scrum.persistence.story;

import org.w3c.dom.Text;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.StoryPriority;
import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "stories")
public class StoryEntity extends BaseEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;

    @Column(name = "business_value", nullable = false)
    private int businessValue;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private StoryPriority priority;

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
}

