package si.smrpo.scrum.persistence.story;

import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "INDEX_TASKS_STORY_ID", columnList = "story_id")
})
@NamedQueries({
    @NamedQuery(name = TaskEntity.GET_BY_STORY, query = "SELECT t FROM TaskEntity t WHERE t.story.id = :storyId")
})
public class TaskEntity extends BaseEntity {
    
    public static final String GET_BY_STORY = "TaskEntity.getByStory";
    
    @Column(name = "description", nullable = false)
    private String description;
    
    @Column(name = "estimate", nullable = false)
    private double estimate;
    
    @Column(name = "completed", nullable = false)
    private boolean completed;
    
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private UserEntity assignee;
    
    @Column(name = "pending_assignment", nullable = false)
    private boolean pendingAssignment;
    
    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private StoryEntity story;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getEstimate() {
        return estimate;
    }
    
    public void setEstimate(double estimate) {
        this.estimate = estimate;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public UserEntity getAssignee() {
        return assignee;
    }
    
    public void setAssignee(UserEntity assignee) {
        this.assignee = assignee;
    }
    
    public StoryEntity getStory() {
        return story;
    }
    
    public void setStory(StoryEntity story) {
        this.story = story;
    }
    
    public boolean isPendingAssignment() {
        return pendingAssignment;
    }
    
    public void setPendingAssignment(boolean pendingAssignment) {
        this.pendingAssignment = pendingAssignment;
    }
    
    public SimpleStatus getStatus() {
        return status;
    }
    
    public void setStatus(SimpleStatus status) {
        this.status = status;
    }
}
