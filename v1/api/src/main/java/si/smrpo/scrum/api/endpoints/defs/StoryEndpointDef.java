package si.smrpo.scrum.api.endpoints.defs;

import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.lib.requests.TaskAssignmentRequest;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.Task;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface StoryEndpointDef {
    
    @GET
    @Path("/{storyId}")
    @Tag(name = "stories")
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "full", in = ParameterIn.QUERY)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Story.class)))
    })
    Response getStoryById(@PathParam("storyId") String storyId,
                          @QueryParam("full") @DefaultValue("false") boolean full);
    
    @PATCH
    @Path("/{storyId}/time-estimate")
    @Tag(name = "stories")
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
    @Parameter(name = "storyId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "taskId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = TaskAssignmentRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response requestTaskForUser(@PathParam("storyId") String storyId, @PathParam("taskId") String taskId, TaskAssignmentRequest request);
    
}


