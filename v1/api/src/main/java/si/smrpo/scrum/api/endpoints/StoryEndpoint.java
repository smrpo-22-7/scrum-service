package si.smrpo.scrum.api.endpoints;

import si.smrpo.scrum.api.endpoints.defs.StoryEndpointDef;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.requests.TaskAssignmentRequest;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.StoryState;
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
    
    @Override
    public Response getStoryState(String storyId) {
        StoryState state = storyService.getStoryState(storyId);
        return Response.ok(state).build();
    }
    
    @Override
    public Response updateTimeEstimate(String storyId, Story story) {
        Story updatedStory = storyService.updateTimeEstimate(storyId, story);
        return Response.ok(updatedStory).build();
    }
    
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
    public Response requestTaskForUser(String storyId, String taskId, TaskAssignmentRequest request) {
        taskService.requestTaskForUser(taskId, request);
        return Response.noContent().build();
    }
    
    @Override
    public Response updateStory(String storyId, CreateStoryRequest request) {
        Story updatedStory = storyService.updateStory(storyId, request);
        return Response.ok(updatedStory).build();
    }
    
    @Override
    public Response deleteStory(String storyId) {
        storyService.removeStory(storyId);
        return Response.noContent().build();
    }
    
}
