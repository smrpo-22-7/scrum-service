package si.smrpo.scrum.persistence.sprint;

import si.smrpo.scrum.persistence.identifiers.SprintStoryId;
import si.smrpo.scrum.persistence.story.StoryEntity;

import javax.persistence.*;

@Entity
@Table(name = "sprint_stories")
@NamedQueries({
    @NamedQuery(name = SprintStoryEntity.GET_STORIES_BY_SPRINT, query = "SELECT s.id.story FROM SprintStoryEntity s WHERE s.id.sprint.id = :sprintId"),
    @NamedQuery(name = SprintStoryEntity.COUNT_STORIES_BY_SPRINT, query = "SELECT COUNT(s.id.story) FROM SprintStoryEntity s WHERE s.id.sprint.id = :sprintId"),
    @NamedQuery(name = SprintStoryEntity.SUM_STORIES_PT_BY_SPRINT, query = "SELECT COALESCE(SUM(s.id.story.timeEstimate), 0) FROM SprintStoryEntity s WHERE s.id.sprint.id = :sprintId")
})
public class SprintStoryEntity {
    
    public static final String GET_STORIES_BY_SPRINT = "SprintStoryEntity.getStoriesBySprint";
    public static final String SUM_STORIES_PT_BY_SPRINT = "SprintStoryEntity.sumStoriesPtBySprint";
    public static final String COUNT_STORIES_BY_SPRINT = "SprintStoryEntity.countStoriesBySprint";

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
