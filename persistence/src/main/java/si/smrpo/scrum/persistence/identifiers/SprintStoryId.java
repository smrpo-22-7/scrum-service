package si.smrpo.scrum.persistence.identifiers;

import si.smrpo.scrum.persistence.sprint.SprintEntity;
import si.smrpo.scrum.persistence.story.StoryEntity;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SprintStoryId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "sprint_id", nullable = false)
    private SprintEntity sprint;

    @ManyToOne
    @JoinColumn(name = "story_id", nullable = false)
    private StoryEntity story;

    public SprintEntity getSprint() {
        return sprint;
    }

    public void setSprint(SprintEntity sprint) {
        this.sprint = sprint;
    }

    public StoryEntity getStory() {
        return story;
    }

    public void setStory(StoryEntity story) {
        this.story = story;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SprintStoryId that = (SprintStoryId) o;
        return sprint.getId().equals(that.sprint.getId()) && story.getId().equals(that.story.getId());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sprint.getId(), story.getId());
    }
}
