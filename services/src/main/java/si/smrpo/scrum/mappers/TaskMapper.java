package si.smrpo.scrum.mappers;

import si.smrpo.scrum.integrations.auth.mappers.UserMapper;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.lib.stories.TaskHour;
import si.smrpo.scrum.lib.stories.TaskWorkSpent;
import si.smrpo.scrum.persistence.story.TaskEntity;
import si.smrpo.scrum.persistence.story.TaskHourEntity;
import si.smrpo.scrum.persistence.story.TaskWorkSpentEntity;

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
    
    public static TaskWorkSpent fromEntity(TaskWorkSpentEntity entity) {
        TaskWorkSpent work = BaseMapper.fromEntity(entity, TaskWorkSpent.class);
        work.setAmount(entity.getAmount());
        if (entity.getWorkDate() != null) {
            work.setWorkDate(entity.getWorkDate().toInstant());
        }
        if (entity.getUser() != null) {
            work.setUserId(entity.getUser().getId());
        }
        if (entity.getTask() != null) {
            TaskWorkSpent.ProjectTask task = new TaskWorkSpent.ProjectTask(
                entity.getTask().getId(),
                entity.getTask().getDescription(),
                entity.getTask().getStory().getProject().getId(),
                entity.getTask().getStory().getProject().getName(),
                entity.getTask().getStory().getId(),
                entity.getTask().getStory().getNumberId()
            );
            work.setTask(task);
        }
        return work;
    }
    
    public static TaskHour fromEntity(TaskHourEntity entity) {
        TaskHour hour = BaseMapper.fromEntity(entity, TaskHour.class);
        
        if (entity.getStartDate() != null) {
            hour.setStartDate(entity.getStartDate().toInstant());
        }
        if (entity.getEndDate() != null) {
            hour.setEndDate(entity.getEndDate().toInstant());
        }
        hour.setAmount(entity.getAmount());
        if (entity.getTask() != null) {
            hour.setTaskId(entity.getTask().getId());
            hour.setTaskName(entity.getTask().getDescription());
            hour.setStoryNumberId(entity.getTask().getStory().getNumberId());
            hour.setStoryId(entity.getTask().getStory().getId());
        }
        return hour;
    }
    
}
