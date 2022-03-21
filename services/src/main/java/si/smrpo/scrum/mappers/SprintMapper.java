package si.smrpo.scrum.mappers;

import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.persistence.sprint.SprintEntity;

public class SprintMapper {

    public static Sprint fromEntity(SprintEntity entity) {
        Sprint sprint = BaseMapper.fromEntity(entity, Sprint.class);
        sprint.setStartDate(entity.getStartDate());
        sprint.setEndDate(entity.getEndDate());
        sprint.setStatus(entity.getStatus());
        sprint.setTitle(entity.getTitle());
        sprint.setExpectedSpeed(entity.getExpectedSpeed());
        if (entity.getProject() != null) {
            sprint.setProjectId(entity.getProject().getId());
        }
        return sprint;
    }

}
