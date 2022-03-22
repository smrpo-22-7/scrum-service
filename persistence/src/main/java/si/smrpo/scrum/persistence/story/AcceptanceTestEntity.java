package si.smrpo.scrum.persistence.story;

import si.smrpo.scrum.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "acceptance_tests", indexes = {
    @Index(name = "INDEX_ACC_TEST_STORY_ID", columnList = "story_id")
})
public class AcceptanceTestEntity extends BaseEntity {
    
    @Column(name = "result", columnDefinition = "TEXT", nullable = false)
    private String result;
    
    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private StoryEntity story;
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
    public StoryEntity getStory() {
        return story;
    }
    
    public void setStory(StoryEntity story) {
        this.story = story;
    }
}
