package si.smrpo.scrum.mappers;

import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.persistence.sprint.SprintEntity;

public class SprintMapper {

    public static Sprint fromEntity(SprintEntity entity) {
        Sprint sprint = BaseMapper.fromEntity(entity, Sprint.class);
        if (entity.getStartDate() != null) {
            sprint.setStartDate(entity.getStartDate().toInstant());
        }
        if (entity.getEndDate() != null) {
            sprint.setEndDate(entity.getEndDate().toInstant());
        }
        sprint.setStatus(entity.getStatus());
        sprint.setTitle(entity.getTitle());
        sprint.setExpectedSpeed(entity.getExpectedSpeed());
        if (entity.getProject() != null) {
            sprint.setProjectId(entity.getProject().getId());
        }
        return sprint;
    }

}
