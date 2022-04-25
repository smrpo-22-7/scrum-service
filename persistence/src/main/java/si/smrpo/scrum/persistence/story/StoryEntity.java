package si.smrpo.scrum.persistence.story;

import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.enums.StoryPriority;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.project.ProjectEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "stories", indexes = {
    @Index(name = "INDEX_STORIES_PROJECT_ID", columnList = "project_id"),
    @Index(name = "UNIQUE_INDEX_NUMBER_ID_PROJECT_ID",
        columnList = "number_id,project_id", unique = true)
})
@NamedQueries({
    @NamedQuery(name = StoryEntity.GET_NEW_NUMBER_ID,
        query = "SELECT COALESCE(MAX(s.numberId) + 1, 1) FROM StoryEntity s WHERE s.project.id = :projectId"),
    @NamedQuery(name = StoryEntity.GET_BY_TITLE, query = "SELECT s FROM StoryEntity s WHERE LOWER(TRIM(BOTH FROM s.title)) = :title AND s.project.id = :projectId"),
    // @formatter:off
    @NamedQuery(name = StoryEntity.CHECK_IN_SPRINT,
        query = "SELECT COUNT(ss) > 0 " +
            "FROM SprintStoryEntity ss " +
            "WHERE ss.id.story.id = :storyId " +
            "AND ss.id.sprint.startDate <= :now " +
            "AND ss.id.sprint.endDate > :now")
    // @formatter:on
})
public class StoryEntity extends BaseEntity {
    
    public static final String GET_NEW_NUMBER_ID = "StoryEntity.getNewNumberId";
    public static final String GET_BY_TITLE = "StoryEntity.getByTitle";
    public static final String CHECK_IN_SPRINT = "StoryEntity.checkInSprint";
    
    
    @Column(name = "title", nullable = false)
    protected String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    protected String description;
    
    @Column(name = "number_id", unique = true, nullable = false, updatable = false)
    protected int numberId;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    protected SimpleStatus status;
    
    @Column(name = "business_value")
    protected Integer businessValue;
    
    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    protected StoryPriority priority;
    
    @Column(name = "time_estimate")
    protected Integer timeEstimate;
    
    @Column(name = "realized")
    protected Boolean realized;
    
    @Column(name = "reject_comment", columnDefinition = "TEXT")
    protected String rejectComment;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    protected ProjectEntity project;
    
    @OneToMany(mappedBy = "story", cascade = {CascadeType.ALL}, orphanRemoval = true)
    protected List<AcceptanceTestEntity> tests;
    
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
    
    public Integer getTimeEstimate() {
        return timeEstimate;
    }
    
    public void setTimeEstimate(Integer timeEstimate) {
        this.timeEstimate = timeEstimate;
    }
    
    public int getNumberId() {
        return numberId;
    }
    
    public void setNumberId(int numberId) {
        this.numberId = numberId;
    }
    
    public Boolean isRealized() {
        return realized;
    }
    
    public void setRealized(Boolean realized) {
        this.realized = realized;
    }
    
    public String getRejectComment() {
        return rejectComment;
    }
    
    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment;
    }
}

