package si.smrpo.scrum.api.endpoints.defs;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.requests.TaskAssignmentRequest;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.StoryState;
import si.smrpo.scrum.lib.stories.Task;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface StoryEndpointDef {
    
    @GET
    @Path("/{storyId}")
    @Tag(name = "stories")
    @Operation(summary = "get story")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "full", in = ParameterIn.QUERY)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Story.class)))
    })
    Response getStoryById(@PathParam("storyId") String storyId,
                          @QueryParam("full") @DefaultValue("false") boolean full);
    
    @GET
    @Path("/{storyId}/state")
    @Tag(name = "stories")
    @Operation(summary = "get story state")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = StoryState.class)))
    })
    Response getStoryState(@PathParam("storyId") String storyId);
    
    @PATCH
    @Path("/{storyId}/time-estimate")
    @Tag(name = "stories")
    @Operation(summary = "update story's time estimate")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Story.class)))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Story.class)))
    })
    Response updateTimeEstimate(@PathParam("storyId") String storyId, Story story);
    
    
    @PATCH
    @Path("/{storyId}/realized")
    @Tag(name = "stories")
    @Operation(summary = "update story's realized status")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Story.class)))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Story.class)))
    })
    Response updateRealized(@PathParam("storyId") String storyId, Story story);
    
    @GET
    @Path("/{storyId}/tasks")
    @Tag(name = "tasks")
    @Operation(summary = "get story tasks")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Task.class, type = SchemaType.ARRAY)))
    })
    Response getStoryTasks(@PathParam("storyId") String storyId);
    
    @POST
    @Path("/{storyId}/tasks")
    @Tag(name = "tasks")
    @Operation(summary = "create story task")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Task.class)))
    @APIResponses({
        @APIResponse(responseCode = "201", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Task.class)))
    })
    Response createStoryTask(@PathParam("storyId") String storyId, Task task);
    
    @PATCH
    @Path("/{storyId}/tasks/{taskId}/request")
    @Tag(name = "tasks")
    @Operation(summary = "request task for user")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "taskId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = TaskAssignmentRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response requestTaskForUser(@PathParam("storyId") String storyId, @PathParam("taskId") String taskId, TaskAssignmentRequest request);
    
    @PATCH
    @Path("/{storyId}")
    @Tag(name = "stories")
    @Operation(summary = "update story")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = CreateStoryRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Story.class)))
    })
    Response updateStory(@PathParam("storyId") String storyId, CreateStoryRequest request);
    
    @DELETE
    @Path("/{storyId}")
    @Tag(name = "stories")
    @Operation(summary = "delete story")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response deleteStory(@PathParam("storyId") String storyId);
    
}


