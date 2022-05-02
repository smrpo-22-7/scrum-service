package si.smrpo.scrum.api.endpoints;

import si.smrpo.scrum.api.endpoints.defs.TaskEndpointDef;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.lib.stories.TaskWorkSpent;
import si.smrpo.scrum.services.TaskService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SecureResource
@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class TaskEndpoint implements TaskEndpointDef {
    
    @Inject
    private TaskService taskService;
    
    @Override
    public Response updateStoryTask(String taskId, Task task) {
        Task updatedTask = taskService.updateTask(taskId, task);
        return Response.ok(updatedTask).build();
    }
    
    @Override
    public Response acceptTaskRequest(String taskId) {
        taskService.acceptTaskRequest(taskId);
        return Response.noContent().build();
    }
    
    @Override
    public Response declineTaskRequest(String taskId) {
        taskService.rejectTaskRequest(taskId);
        return Response.noContent().build();
    }
    
    @Override
    public Response clearAssignee(String taskId) {
        taskService.clearAssignee(taskId);
        return Response.noContent().build();
    }
    
    @Override
    public Response removeTask(String taskId) {
        taskService.removeTask(taskId);
        return Response.noContent().build();
    }
    
    @Override
    public Response startWorkingOnTask(String taskId) {
        taskService.startWorkOnTask(taskId);
        return Response.noContent().build();
    }
    
    @Override
    public Response getTaskHours(String taskId) {
        return Response.ok(taskService.getTaskHours(taskId)).build();
    }
    
    @Override
    public Response updateTaskHours(String taskId, TaskWorkSpent taskWork) {
        this.taskService.updateTaskHoursByDate(taskId, taskWork);
        return Response.noContent().build();
    }
    
}
