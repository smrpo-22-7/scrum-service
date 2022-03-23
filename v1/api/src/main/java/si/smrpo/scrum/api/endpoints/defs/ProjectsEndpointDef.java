package si.smrpo.scrum.api.endpoints.defs;

import com.mjamsek.rest.Rest;
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
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.projects.ProjectRole;
import si.smrpo.scrum.lib.requests.ConflictCheckRequest;
import si.smrpo.scrum.lib.requests.CreateProjectRequest;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.responses.ProjectRolesCount;
import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.lib.stories.Story;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface ProjectsEndpointDef {
    
    @GET
    @Tag(name = "projects")
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
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectRole.class, type = SchemaType.ARRAY)))
    })
    Response getAllProjectRoles();
    
    @GET
    @Path("/my-projects")
    @Tag(name = "projects")
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
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Project.class)))
    })
    Response getProjectById(@PathParam("projectId") String projectId);
    
    @POST
    @Tag(name = "projects")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = CreateProjectRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "201", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Project.class)))
    })
    Response createProject(CreateProjectRequest request);
    
    @POST
    @Path("/name-check")
    @Tag(name = "projects")
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ConflictCheckRequest.class)))
    @APIResponses({
        @APIResponse(responseCode = "204"),
        @APIResponse(responseCode = "409"),
    })
    Response checkProjectNameExits(ConflictCheckRequest request);
    
    @PATCH
    @Path("/{projectId}")
    @Tag(name = "projects")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Project.class)))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Project.class)))
    })
    Response updateProjectName(@PathParam("projectId") String projectId, Project project);
    
    @DELETE
    @Path("/{projectId}/disable")
    @Tag(name = "projects")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response setProjectStatusDisabled(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/activate")
    @Tag(name = "projects")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response setProjectStatusActivated(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/roles/count")
    @Tag(name = "projects")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectRolesCount.class)))
    })
    Response getProjectRolesCount(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/users")
    @Tag(name = "projects")
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
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "userId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response removeUserFromProject(@PathParam("projectId") String projectId, @PathParam("userId") String userId);
    
    @PATCH
    @Path("/{projectId}/users/{userId}")
    @Tag(name = "projects")
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
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Story.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getStories(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/stories")
    @Tag(name = "stories")
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
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Sprint.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getProjectSprints(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/sprints")
    @Tag(name = "sprints")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = Sprint.class)))
    @APIResponses({
        @APIResponse(responseCode = "201", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = Sprint.class)))
    })
    Response createSprint(@PathParam("projectId") String projectId, Sprint sprint);
}
