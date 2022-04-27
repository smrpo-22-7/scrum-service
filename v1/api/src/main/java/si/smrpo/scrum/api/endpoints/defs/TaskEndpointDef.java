package si.smrpo.scrum.api.endpoints.defs;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.lib.stories.Task;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface TaskEndpointDef {
    
    @PATCH
    @Path("/{taskId}")
    @Tag(name = "tasks")
    @Operation(summary = "update story task")
    @Parameter(name = "taskId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Task.class)))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Task.class)))
    })
    Response updateStoryTask(@PathParam("taskId") String taskId, Task task);
    
    
    @POST
    @Path("/{taskId}/request")
    @Tag(name = "tasks")
    @Operation(summary = "accept task request")
    @Parameter(name = "taskId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response acceptTaskRequest(@PathParam("taskId") String taskId);
    
    @DELETE
    @Path("/{taskId}/request")
    @Tag(name = "tasks")
    @Operation(summary = "decline task request")
    @Parameter(name = "taskId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response declineTaskRequest(@PathParam("taskId") String taskId);
    
    @DELETE
    @Path("/{taskId}/assignee")
    @Tag(name = "tasks")
    @Operation(summary = "clear assigned user")
    @Parameter(name = "taskId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response clearAssignee(@PathParam("taskId") String taskId);
    
    @DELETE
    @Path("/{taskId}")
    @Tag(name = "tasks")
    @Operation(summary = "remove task")
    @Parameter(name = "taskId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response removeTask(@PathParam("taskId") String taskId);
    
    @POST
    @Path("/{taskId}/start-work")
    @Tag(name = "tasks")
    @Operation(summary = "start working on task")
    @Parameter(name = "taskId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response startWorkingOnTask(@PathParam("taskId") String taskId);
    
    @POST
    @Path("/end-active-task")
    @Tag(name = "tasks")
    @Operation(summary = "stop working on task")
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response startWorkingOnTask();
    
}
