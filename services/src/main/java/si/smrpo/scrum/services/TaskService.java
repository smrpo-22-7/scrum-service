package si.smrpo.scrum.services;

import si.smrpo.scrum.lib.requests.TaskAssignmentRequest;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.persistence.story.TaskEntity;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    
    List<Task> getStoryTasks(String storyId);
    
    Optional<TaskEntity> getTaskEntityById(String taskId);
    
    Task createTask(String storyId, Task task);
    
    Task updateTask(String taskId, Task task);
    
    void removeTask(String taskId);
    
    void requestTaskForUser(String taskId, TaskAssignmentRequest request);
    
    void acceptTaskRequest(String taskId);
    
    void rejectTaskRequest(String taskId);
    
}
