package si.smrpo.scrum.persistence.sprint;

import si.smrpo.scrum.persistence.identifiers.SprintStoryId;
import si.smrpo.scrum.persistence.story.StoryEntity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "sprint_stories")
public class SprintStoryEntity {

    @EmbeddedId
    private SprintStoryId id;

    public SprintStoryId getId() {
        return id;
    }

    public void setId(SprintStoryId id) {
        this.id = id;
    }

    public SprintEntity getSprint(){
        return this.id.getSprint();
    }

    public StoryEntity getStory(){
        return this.id.getStory();
    }
}
