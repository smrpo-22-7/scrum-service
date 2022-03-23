package si.smrpo.scrum.persistence.identifiers;

import si.smrpo.scrum.persistence.sprint.SprintEntity;
import si.smrpo.scrum.persistence.story.StoryEntity;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class SprintStoryId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private SprintEntity sprint;

    @ManyToOne
    @JoinColumn(name = "story_id")
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
}
