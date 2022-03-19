package si.smrpo.scrum.api.endpoints.defs;

import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.requests.ConflictCheckRequest;
import si.smrpo.scrum.lib.requests.CreateProjectRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

public interface ProjectsEndpointDef {

    @GET
    Response getProjectsList();

    @GET
    @Path("/my-projects")
    Response getUserProjects();

    @GET
    @Path("/{projectId}")
    Response getProjectById(@PathParam("projectId") String projectId);

    @POST
    Response createProject(CreateProjectRequest request);

    @POST
    @Path("/name-check")
    Response checkProjectNameExits(ConflictCheckRequest request);

    @PATCH
    @Path("/{projectId}")
    Response updateProjectName(@PathParam("projectId") String projectId, Project project);

    @DELETE
    @Path("/{projectId}/disable")
    Response setProjectStatusDisabled(@PathParam("projectId") String projectId);

    @POST
    @Path("/{projectId}/activate")
    Response setProjectStatusActivated(@PathParam("projectId") String projectId);

    @POST
    @Path("/{projectId}/users")
    Response addUserToProject(@PathParam("projectId") String projectId, ProjectMember member);

    @DELETE
    @Path("/{projectId}/users/{userId}")
    Response removeUserFromProject(@PathParam("projectId") String projectId, @PathParam("userId") String userId);

    @PATCH
    @Path("/{projectId}/users/{userId}")
    Response updateUserProjectRole(@PathParam("projectId") String projectId, @PathParam("userId") String userId, ProjectMember member);
}
