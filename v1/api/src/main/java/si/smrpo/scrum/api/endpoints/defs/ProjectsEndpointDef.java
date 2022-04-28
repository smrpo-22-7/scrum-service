package si.smrpo.scrum.api.endpoints.defs;

import com.kumuluz.ee.rest.enums.OrderDirection;
import com.mjamsek.rest.Rest;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.api.params.ProjectSprintFilters;
import si.smrpo.scrum.api.params.ProjectStoriesParams;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.projects.ProjectRole;
import si.smrpo.scrum.lib.requests.ConflictCheckRequest;
import si.smrpo.scrum.lib.requests.ProjectRequest;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.requests.SprintConflictCheckRequest;
import si.smrpo.scrum.lib.responses.ExtendedStory;
import si.smrpo.scrum.lib.responses.ProjectRolesCount;
import si.smrpo.scrum.lib.responses.SprintStatus;
import si.smrpo.scrum.lib.responses.SprintListResponse;
import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.lib.stories.ExtendedTask;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.TaskHour;
import si.smrpo.scrum.lib.stories.TaskWorkSpent;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface ProjectsEndpointDef {
    
    @GET
    @Tag(name = "projects")
    @Operation(summary = "get projects list")
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Project.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getProjectsList();
    
    @GET
    @Path("/roles")
    @Tag(name = "projects")
    @Operation(summary = "get project roles")
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectRole.class, type = SchemaType.ARRAY)))
    })
    Response getAllProjectRoles();
    
    @GET
    @Path("/my-projects")
    @Tag(name = "projects")
    @Operation(summary = "get user projects")
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Project.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getUserProjects();
    
    @GET
    @Path("/{projectId}")
    @Tag(name = "projects")
    @Operation(summary = "get project")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Project.class)))
    })
    Response getProjectById(@PathParam("projectId") String projectId);
    
    @POST
    @Tag(name = "projects")
    @Operation(summary = "create project")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ProjectRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "201", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Project.class)))
    })
    Response createProject(ProjectRequest request);
    
    @POST
    @Path("/name-check")
    @Tag(name = "projects")
    @Operation(summary = "check project name conflict")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ConflictCheckRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "204"),
        @APIResponse(responseCode = "409"),
    })
    Response checkProjectNameExits(ConflictCheckRequest request);
    
    @PUT
    @Path("/{projectId}")
    @Tag(name = "projects")
    @Operation(summary = "update project")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ProjectRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Project.class)))
    })
    Response updateProject(@PathParam("projectId") String projectId, ProjectRequest request);
    
    @DELETE
    @Path("/{projectId}/disable")
    @Tag(name = "projects")
    @Operation(summary = "disable project")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response setProjectStatusDisabled(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/activate")
    @Tag(name = "projects")
    @Operation(summary = "reactivate project")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response setProjectStatusActivated(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/roles/count")
    @Tag(name = "projects")
    @Operation(summary = "get project roles count")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectRolesCount.class)))
    })
    Response getProjectRolesCount(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/roles/user")
    @Tag(name = "projects")
    @Operation(summary = "get user project role")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectRole.class)))
    })
    Response getUserProjectRoles(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/users")
    @Tag(name = "projects")
    @Operation(summary = "add user to project")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ProjectMember.class)))
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response addUserToProject(@PathParam("projectId") String projectId, ProjectMember member);
    
    @DELETE
    @Path("/{projectId}/users/{userId}")
    @Tag(name = "projects")
    @Operation(summary = "remove user from project")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "userId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response removeUserFromProject(@PathParam("projectId") String projectId, @PathParam("userId") String userId);
    
    @PATCH
    @Path("/{projectId}/users/{userId}")
    @Tag(name = "projects")
    @Operation(summary = "update user project role")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "userId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ProjectMember.class)))
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response updateUserProjectRole(@PathParam("projectId") String projectId, @PathParam("userId") String userId, ProjectMember member);
    
    @GET
    @Path("/{projectId}/stories")
    @Tag(name = "stories")
    @Operation(summary = "get project stories (extended)")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "numberIdSort", in = ParameterIn.QUERY, schema = @Schema(implementation = OrderDirection.class))
    @Parameter(name = "filterRealized", in = ParameterIn.QUERY, schema = @Schema(implementation = Boolean.class))
    @Parameter(name = "filterAssigned", in = ParameterIn.QUERY, schema = @Schema(implementation = Boolean.class))
    @Parameter(name = "limit", in = ParameterIn.QUERY, schema = @Schema(implementation = Long.class))
    @Parameter(name = "offset", in = ParameterIn.QUERY, schema = @Schema(implementation = Long.class))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ExtendedStory.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getProjectStories(@PathParam("projectId") String projectId, @BeanParam ProjectStoriesParams params);
    
    @POST
    @Path("/{projectId}/stories")
    @Tag(name = "stories")
    @Operation(summary = "create story")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = CreateStoryRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "201", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Story.class)))
    })
    Response createStory(@PathParam("projectId") String projectId, CreateStoryRequest request);
    
    @GET
    @Path("/{projectId}/sprints")
    @Tag(name = "sprints")
    @Operation(summary = "get project sprints")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "active", in = ParameterIn.QUERY, schema = @Schema(type = SchemaType.BOOLEAN))
    @Parameter(name = "future", in = ParameterIn.QUERY, schema = @Schema(type = SchemaType.BOOLEAN))
    @Parameter(name = "past", in = ParameterIn.QUERY, schema = @Schema(type = SchemaType.BOOLEAN))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = SprintListResponse.class)))
    })
    Response getProjectSprints(@PathParam("projectId") String projectId, @BeanParam ProjectSprintFilters filters);
    
    @POST
    @Path("/{projectId}/sprints")
    @Tag(name = "sprints")
    @Operation(summary = "create sprint")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Sprint.class)))
    @APIResponses({
        @APIResponse(responseCode = "201", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Sprint.class)))
    })
    Response createSprint(@PathParam("projectId") String projectId, Sprint sprint);
    
    @POST
    @Path("/{projectId}/sprints/check")
    @Tag(name = "stories")
    @Operation(summary = "check sprint date conflicts")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = SprintConflictCheckRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "204"),
        @APIResponse(responseCode = "409"),
    })
    Response checkSprintDates(@PathParam("projectId") String projectId, SprintConflictCheckRequest request);
    
    @POST
    @Path("/{projectId}/stories/name-check")
    @Tag(name = "stories")
    @Operation(summary = "check story name conflict")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ConflictCheckRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "204"),
        @APIResponse(responseCode = "409"),
    })
    Response checkStoryNameExits(@PathParam("projectId") String projectId, ConflictCheckRequest request);
    
    @GET
    @Path("/{projectId}/members")
    @Tag(name = "projects")
    @Operation(summary = "get project members")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectMember.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getProjectMembers(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/members/query")
    @Tag(name = "projects")
    @Operation(summary = "query project members")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = UserProfile.class, type = SchemaType.ARRAY)))
    })
    Response queryProjectMembers(@PathParam("projectId") String projectId,
                                 @QueryParam("query") String query);
    
    @GET
    @Path("/{projectId}/sprints/status")
    @Tag(name = "projects")
    @Operation(summary = "get project's active sprint's status")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = SprintStatus.class)))
    })
    Response getProjectsActiveSprintStatus(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/hours")
    @Tag(name = "hours")
    @Operation(summary = "get user hours")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", description = "returns user's hours", content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = TaskWorkSpent.class, type = SchemaType.ARRAY)),
            headers = {
                @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, description = "Number of all elements matching query", schema = @Schema(type = SchemaType.INTEGER))
            })
    })
    Response getUserHours(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/active-task")
    @Tag(name = "tasks")
    @Operation(summary = "get active task")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", description = "returns user's active task", content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = TaskHour.class)))
    })
    Response getActiveTask(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/end-active-task")
    @Tag(name = "tasks")
    @Operation(summary = "stop working on task")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response stopWorkingOnTask(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/tasks")
    @Tag(name = "tasks")
    @Operation(summary = "get project's active sprint task")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", description = "returns project's task", content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(implementation = ExtendedTask.class)))
    })
    Response getProjectTasks(@PathParam("projectId") String projectId);
}
