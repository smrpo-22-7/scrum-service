package si.smrpo.scrum.api.endpoints.defs;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.requests.ConflictCheckRequest;
import si.smrpo.scrum.lib.requests.CreateProjectRequest;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.sprints.Sprint;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

public interface ProjectsEndpointDef {

    @GET
    @Tag(name = "projects")
    Response getProjectsList();
    
    @GET
    @Path("/roles")
    @Tag(name = "projects")
    Response getAllProjectRoles();

    @GET
    @Path("/my-projects")
    @Tag(name = "projects")
    Response getUserProjects();

    @GET
    @Path("/{projectId}")
    @Tag(name = "projects")
    Response getProjectById(@PathParam("projectId") String projectId);

    @POST
    @Tag(name = "projects")
    Response createProject(CreateProjectRequest request);

    @POST
    @Path("/name-check")
    @Tag(name = "projects")
    Response checkProjectNameExits(ConflictCheckRequest request);

    @PATCH
    @Path("/{projectId}")
    @Tag(name = "projects")
    Response updateProjectName(@PathParam("projectId") String projectId, Project project);

    @DELETE
    @Path("/{projectId}/disable")
    @Tag(name = "projects")
    Response setProjectStatusDisabled(@PathParam("projectId") String projectId);

    @POST
    @Path("/{projectId}/activate")
    @Tag(name = "projects")
    Response setProjectStatusActivated(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/roles/count")
    @Tag(name = "projects")
    Response getProjectRolesCount(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/users")
    @Tag(name = "projects")
    Response addUserToProject(@PathParam("projectId") String projectId, ProjectMember member);

    @DELETE
    @Path("/{projectId}/users/{userId}")
    @Tag(name = "projects")
    Response removeUserFromProject(@PathParam("projectId") String projectId, @PathParam("userId") String userId);

    @PATCH
    @Path("/{projectId}/users/{userId}")
    @Tag(name = "projects")
    Response updateUserProjectRole(@PathParam("projectId") String projectId, @PathParam("userId") String userId, ProjectMember member);

    @GET
    @Path("/{projectId}/stories")
    @Tag(name = "stories")
    Response getStories(@PathParam("projectId") String projectId);

    @POST
    @Path("/{projectId}/stories")
    @Tag(name = "stories")
    Response createStory(@PathParam("projectId") String projectId, CreateStoryRequest request);
    
    @GET
    @Path("/{projectId}/sprints")
    @Tag(name = "sprints")
    Response getProjectSprints(@PathParam("projectId") String projectId);
    
    @POST
    @Path("/{projectId}/sprints")
    @Tag(name = "sprints")
    Response createSprint(@PathParam("projectId") String projectId, Sprint sprint);
}
