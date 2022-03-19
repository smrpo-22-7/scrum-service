package si.smrpo.scrum.api.endpoints;


import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.api.endpoints.defs.ProjectsEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.lib.projects.Project;
import si.smrpo.scrum.lib.projects.ProjectMember;
import si.smrpo.scrum.lib.requests.ConflictCheckRequest;
import si.smrpo.scrum.lib.requests.CreateProjectRequest;
import si.smrpo.scrum.services.ProjectService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.mjamsek.rest.Rest.HttpHeaders.X_TOTAL_COUNT;

@SecureResource
@Path("/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ProjectsEndpoint implements ProjectsEndpointDef {

    @Inject
    private QueryParameters queryParameters;

    @Inject
    private AuthContext authContext;

    @Inject
    private ProjectService projectService;

    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response getProjectsList() {
        EntityList<Project> projects = projectService.getProjects(queryParameters);
        return Response.ok(projects.getEntities())
                .header(X_TOTAL_COUNT, projects.getCount())
                .build();
    }

    @Override
    public Response getUserProjects() {
        EntityList<Project> projects = projectService.getUserProjects(authContext.getId(), queryParameters);
        return Response.ok(projects.getEntities())
                .header(X_TOTAL_COUNT, projects.getCount())
                .build();
    }

    @Override
    public Response getProjectById(String projectId) {
        Project project = projectService.getProjectById(projectId);
        return Response.ok(project).build();
    }

    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response createProject(CreateProjectRequest request) {
        Project createdProject = projectService.createProject(request);
        return Response.status(Response.Status.CREATED).entity(createdProject).build();
    }

    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response checkProjectNameExits(ConflictCheckRequest request) {
        boolean exists = projectService.projectNameExists(request.getValue());
        if (exists) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.noContent().build();
    }

    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response updateProjectName(String projectId, Project project) {
        Project updatedProject = projectService.updateProject(projectId, project);
        return Response.ok(updatedProject).build();
    }

    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response setProjectStatusDisabled(String projectId) {
        projectService.changeProjectStatus(projectId, SimpleStatus.DISABLED);
        return Response.noContent().build();
    }

    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response setProjectStatusActivated(String projectId) {
        projectService.changeProjectStatus(projectId, SimpleStatus.ACTIVE);
        return Response.noContent().build();
    }

    @Override
    public Response addUserToProject(String projectId, ProjectMember member) {
        projectService.addUserToProject(projectId, member);
        return Response.noContent().build();
    }

    @Override
    public Response removeUserFromProject(String projectId, String userId) {
        projectService.removeUserFromProject(projectId, userId);
        return Response.noContent().build();
    }

    @Override
    public Response updateUserProjectRole(String projectId, String userId, ProjectMember member) {
        projectService.updateUserProjectRole(projectId, userId, member);
        return Response.noContent().build();
    }

}
