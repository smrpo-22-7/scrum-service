package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.requests.AddStoryRequest;
import si.smrpo.scrum.lib.responses.SprintListResponse;
import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.persistence.sprint.SprintEntity;

import java.util.Optional;

public interface SprintService {

    EntityList<Sprint> getSprints(QueryParameters queryParameters);
    
    EntityList<Story> getSprintStories(String sprintId, QueryParameters queryParameters);
    
    SprintListResponse getProjectSprints(String projectId, boolean active, boolean past, boolean future);
    
    Sprint getSprintById(String sprintId);

    Optional<SprintEntity> getSprintEntityById(String sprintId);

    Sprint createSprint(String projectId, Sprint sprint);

    void addStoriesToSprint(String sprintId, AddStoryRequest request);
}
