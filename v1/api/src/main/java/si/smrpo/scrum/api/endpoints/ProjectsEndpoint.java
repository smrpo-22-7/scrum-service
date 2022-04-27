package si.smrpo.scrum.api.endpoints;


import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.Rest;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.api.endpoints.defs.ProjectsEndpointDef;
import si.smrpo.scrum.api.params.ProjectSprintFilters;
import si.smrpo.scrum.api.params.ProjectStoriesParams;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.AuthContext;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.enums.SimpleStatus;
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
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.lib.stories.TaskWorkSpent;
import si.smrpo.scrum.services.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Set;

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
    
    @Inject
    private ProjectMembershipService projectMembershipService;

    @Inject
    private StoryService storyService;
    
    @Inject
    private SprintService sprintService;
    
    @Inject
    private TaskService taskService;
    
    @Inject
    private ProjectAuthorizationService projectAuthorizationService;

    @SysRolesRequired({Roles.ADMIN_ROLE})
    @Override
    public Response getProjectsList() {
        EntityList<Project> projects = projectService.getProjects(queryParameters);
        return Response.ok(projects.getEntities())
                .header(X_TOTAL_COUNT, projects.getCount())
                .build();
    }
    
    @Override
    public Response getAllProjectRoles() {
        Set<ProjectRole> roles = projectService.getAllProjectRoles();
        return Response.ok(roles).build();
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
    public Response createProject(ProjectRequest request) {
        Project createdProject = projectService.createProject(request);
        return Response.status(Response.Status.CREATED).entity(createdProject).build();
    }
    
    @Override
    public Response checkProjectNameExits(ConflictCheckRequest request) {
        boolean exists = projectService.projectNameExists(request.getValue());
        if (exists) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.noContent().build();
    }
    
    @Override
    public Response updateProject(String projectId, ProjectRequest project) {
        Project updatedProject = projectService.updateProject(projectId, project);
        return Response.ok(updatedProject).build();
    }
    
    @Override
    public Response setProjectStatusDisabled(String projectId) {
        projectService.changeProjectStatus(projectId, SimpleStatus.DISABLED);
        return Response.noContent().build();
    }
    
    @Override
    public Response setProjectStatusActivated(String projectId) {
        projectService.changeProjectStatus(projectId, SimpleStatus.ACTIVE);
        return Response.noContent().build();
    }
    
    @Override
    public Response getProjectRolesCount(String projectId) {
        ProjectRolesCount counts = projectMembershipService.getProjectRolesCount(projectId);
        return Response.ok(counts).build();
    }
    
    @Override
    public Response getUserProjectRoles(String projectId) {
        ProjectRole role = projectAuthorizationService.getUserProjectRole(projectId, authContext.getId());
        return Response.ok(role).build();
    }
    
    @Override
    public Response addUserToProject(String projectId, ProjectMember member) {
        projectMembershipService.addUserToProject(projectId, member);
        return Response.noContent().build();
    }
    
    @Override
    public Response removeUserFromProject(String projectId, String userId) {
        projectMembershipService.removeUserFromProject(projectId, userId);
        return Response.noContent().build();
    }
    
    @Override
    public Response updateUserProjectRole(String projectId, String userId, ProjectMember member) {
        projectMembershipService.updateUserProjectRole(projectId, userId, member);
        return Response.noContent().build();
    }

    
    @Override
    public Response createStory(String projectId, CreateStoryRequest request) {
        Story newStory = storyService.createStory(projectId, request);
        return Response.status(Response.Status.CREATED).entity(newStory).build();
    }
    
    @Override
    public Response getProjectSprints(String projectId, ProjectSprintFilters filters) {
        SprintListResponse response = sprintService.getProjectSprints(projectId,
            filters.isActive(), filters.isPast(), filters.isFuture());
        return Response.ok(response).build();
    }
    
    @Override
    public Response createSprint(String projectId, Sprint sprint) {
        Sprint createdSprint = sprintService.createSprint(projectId, sprint);
        return Response.status(Response.Status.CREATED).entity(createdSprint).build();
    }
    
    @Override
    public Response checkSprintDates(String projectId, SprintConflictCheckRequest request) {
        boolean exists = sprintService.checkForDateConflicts(projectId, request);
        if (exists) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.noContent().build();
    }
    
    @Override
    public Response checkStoryNameExits(String projectId, ConflictCheckRequest request) {
        boolean exists = storyService.checkStoryNameExists(projectId, request);
        if (exists) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        return Response.noContent().build();
    }
    
    @Override
    public Response getProjectMembers(String projectId) {
        EntityList<ProjectMember> members = projectMembershipService.getProjectMembers(projectId, queryParameters);
        return Response.ok(members.getEntities())
            .header(X_TOTAL_COUNT, members.getCount())
            .build();
    }
    
    @Override
    public Response queryProjectMembers(String projectId, String query) {
        List<UserProfile> members = projectMembershipService.queryProjectMembers(projectId, query);
        return Response.ok(members).build();
    }
    
    @Override
    public Response getProjectsActiveSprintStatus(String projectId) {
        SprintStatus status = sprintService.getProjectActiveSprintStatus(projectId);
        return Response.ok(status).build();
    }
    
    @Override
    public Response getProjectStories(String projectId, ProjectStoriesParams params) {
        EntityList<ExtendedStory> stories = storyService.getProjectStories(projectId, params.toProjectStoriesFilters());
        return Response.ok(stories.getEntities()).header(X_TOTAL_COUNT, stories.getCount()).build();
    }
    
    @Override
    public Response getUserHours(String projectId) {
        EntityList<TaskWorkSpent> hours = taskService.getCurrentUserTaskWorkSpent(projectId, queryParameters);
        return Response.ok(hours.getEntities())
            .header(Rest.HttpHeaders.X_TOTAL_COUNT, hours.getCount())
            .build();
    }
}
