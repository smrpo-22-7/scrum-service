package si.smrpo.scrum.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.lib.requests.TaskAssignmentRequest;
import si.smrpo.scrum.lib.stories.ExtendedTask;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.lib.stories.TaskHour;
import si.smrpo.scrum.lib.stories.TaskWorkSpent;
import si.smrpo.scrum.persistence.story.TaskEntity;
import si.smrpo.scrum.persistence.story.TaskHourEntity;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    
    List<ExtendedTask> getStoryTasks(String storyId);
    
    EntityList<ExtendedTask> getActiveSprintTasks(String projectId, QueryParameters queryParameters);
    
    Optional<TaskEntity> getTaskEntityById(String taskId);
    
    Task createTask(String storyId, Task task);
    
    Task updateTask(String taskId, Task task);
    
    void removeTask(String taskId);
    
    void requestTaskForUser(String taskId, TaskAssignmentRequest request);
    
    void acceptTaskRequest(String taskId);
    
    void rejectTaskRequest(String taskId);
    
    void clearAssignee(String taskId);
    
    void startWorkOnTask(String taskId);
    
    void endWorkOnTask(String projectId);
    
    TaskHour getUserActiveTask(String projectId);
    
    TaskWorkSpent updateTaskHours(String hourId, TaskWorkSpent taskWork);
    
    List<TaskWorkSpent> getTaskHours(String taskId);
    
    void removeTaskHours(String hourId);
    
    Optional<TaskHourEntity> getUserActiveTaskEntity(String projectId);
    
    EntityList<TaskWorkSpent> getUserTaskWorkSpent(String projectId, String userId, QueryParameters queryParameters);
    
    EntityList<TaskWorkSpent> getCurrentUserTaskWorkSpent(String projectId, QueryParameters queryParameters);
    
}
