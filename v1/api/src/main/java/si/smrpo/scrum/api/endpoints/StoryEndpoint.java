package si.smrpo.scrum.api.endpoints;

import si.smrpo.scrum.api.endpoints.defs.StoryEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.lib.requests.TaskAssignmentRequest;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.Task;
import si.smrpo.scrum.services.StoryService;
import si.smrpo.scrum.services.TaskService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@SecureResource
@Path("/stories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped

public class StoryEndpoint implements StoryEndpointDef {
    
    @Inject
    private StoryService storyService;
    
    @Inject
    private TaskService taskService;
    
    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response getStoryById(String storyId, boolean full) {
        Story story;
        if (full) {
            story = storyService.getFullStoryById(storyId);
        } else {
            story = storyService.getStoryById(storyId);
        }
        return Response.ok(story).build();
    }
    
    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response updateTimeEstimate(String storyId, Story story) {
        Story updatedStory = storyService.updateTimeEstimate(storyId, story);
        return Response.ok(updatedStory).build();
    }
    
    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response updateRealized(String storyId, Story story) {
        Story updatedStory = storyService.updateRealized(storyId, story);
        return Response.ok(updatedStory).build();
    }
    
    @Override
    public Response getStoryTasks(String storyId) {
        List<Task> tasks = taskService.getStoryTasks(storyId);
        return Response.ok(tasks).build();
    }
    
    @Override
    public Response createStoryTask(String storyId, Task task) {
        Task createdTask = taskService.createTask(storyId, task);
        return Response.status(Response.Status.CREATED).entity(createdTask).build();
    }
    
    @Override
    public Response updateStoryTask(String storyId, String taskId, Task task) {
        Task updatedTask = taskService.updateTask(storyId, task);
        return Response.ok(updatedTask).build();
    }
    
    @Override
    public Response removeStoryTask(String storyId, String taskId) {
        taskService.removeTask(taskId);
        return Response.noContent().build();
    }
    
    @Override
    public Response requestTaskForUser(String storyId, String taskId, TaskAssignmentRequest request) {
        taskService.requestTaskForUser(taskId, request);
        return Response.noContent().build();
    }
    
    @Override
    public Response acceptTaskRequest(String storyId, String taskId) {
        taskService.acceptTaskRequest(taskId);
        return Response.noContent().build();
    }
    
    @Override
    public Response declineTaskRequest(String storyId, String taskId) {
        taskService.rejectTaskRequest(taskId);
        return Response.noContent().build();
    }
    
}
