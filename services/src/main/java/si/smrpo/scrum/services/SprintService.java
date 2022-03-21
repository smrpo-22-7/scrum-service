package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.persistence.sprint.SprintEntity;

import java.util.Optional;

public interface SprintService {

    EntityList<Sprint> getSprints(QueryParameters queryParameters);

    Sprint getSprintById(String sprintId);

    Optional<SprintEntity> getSprintEntityById(String sprintId);

    Sprint createSprint(Sprint sprint);
}
