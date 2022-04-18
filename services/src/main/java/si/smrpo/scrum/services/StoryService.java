package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.params.ProjectStoriesFilters;
import si.smrpo.scrum.lib.requests.ConflictCheckRequest;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.responses.ExtendedStory;
import si.smrpo.scrum.lib.stories.AcceptanceTest;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.StoryState;
import si.smrpo.scrum.persistence.story.StoryEntity;

import java.util.List;
import java.util.Optional;

public interface StoryService {

    EntityList<Story> getStories(String projectId, QueryParameters queryParameters);
    
    EntityList<ExtendedStory> getProjectStories(String projectId, ProjectStoriesFilters params);

    Story getStoryById(String storyId);
    
    StoryState getStoryState(String storyId);
    
    Story getFullStoryById(String storyId);

    Optional<StoryEntity> getStoryEntityById(String storyId);

    Story createStory(String projectId, CreateStoryRequest request);
    
    Story updateTimeEstimate(String storyId, Story story);
    
    List<AcceptanceTest> getStoryAcceptanceTests(String storyId);

    Story updateRealized(String storyId, Story story);
    
    boolean checkStoryNameExists(String projectId, ConflictCheckRequest request);
}
