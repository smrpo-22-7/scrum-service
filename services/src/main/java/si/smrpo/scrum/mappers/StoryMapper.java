package si.smrpo.scrum.mappers;

import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.AcceptanceTest;
import si.smrpo.scrum.persistence.story.StoryEntity;
import si.smrpo.scrum.persistence.story.AcceptanceTestEntity;

import java.util.stream.Collectors;

public class StoryMapper {

    public static Story fromEntity(StoryEntity entity) {
        Story story = BaseMapper.fromEntity(entity, Story.class);
        story.setStatus(entity.getStatus());
        story.setDescription(entity.getDescription());
        story.setBusinessValue(entity.getBusinessValue());
        story.setPriority(entity.getPriority());
        story.setTitle(entity.getTitle());
        story.setNumberId(entity.getNumberId());
        story.setTimeEstimate(entity.getTimeEstimate());
        if (entity.isRealized() != null) {
            story.setRealized(entity.isRealized());
        } else {
            story.setRealized(false);
        }
        if (entity.getTests() != null) {
            story.setTests(entity.getTests().stream().map(StoryMapper::fromEntity).collect(Collectors.toList()));
        }
        if (entity.getProject() != null) {
            story.setProjectId(entity.getProject().getId());
        }
        
        return story;
    }

    public static AcceptanceTest fromEntity(AcceptanceTestEntity entity) {
        AcceptanceTest acceptanceTest = BaseMapper.fromEntity(
                entity, AcceptanceTest.class);
        acceptanceTest.setResult(entity.getResult());
        if(entity.getStory() != null) {
            acceptanceTest.setStoryId(entity.getStory().getId());
        }

        return acceptanceTest;
    }
}
