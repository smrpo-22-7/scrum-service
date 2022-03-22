package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.persistence.story.StoryEntity;

import java.util.Optional;

public interface StoryService {

    EntityList<Story> getStories(String projectId, QueryParameters queryParameters);

    Story getStoryById(String storyId);

    Optional<StoryEntity> getStoryEntityById(String storyId);

    Story createStory(String projectId, CreateStoryRequest request);
}
