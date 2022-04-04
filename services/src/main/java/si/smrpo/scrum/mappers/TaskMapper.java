package si.smrpo.scrum.mappers;

import si.smrpo.scrum.integrations.auth.mappers.UserMapper;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.persistence.story.TaskEntity;

public class TaskMapper {
    
    public static Task fromEntity(TaskEntity entity) {
        Task task = BaseMapper.fromEntity(entity, Task.class);
        task.setCompleted(entity.isCompleted());
        task.setEstimate(entity.getEstimate());
        task.setDescription(entity.getDescription());
        task.setStatus(entity.getStatus());
    
        Task.TaskAssignment assignment = new Task.TaskAssignment();
        if (entity.getAssignee() != null) {
            assignment.setAssignee(UserMapper.toSimpleProfile(entity.getAssignee()));
            assignment.setAssigneeId(entity.getAssignee().getId());
            assignment.setPending(entity.isPendingAssignment());
        }
        task.setAssignment(assignment);
        if (entity.getStory() != null) {
            task.setStoryId(entity.getStory().getId());
        }
        return task;
    }
    
}
