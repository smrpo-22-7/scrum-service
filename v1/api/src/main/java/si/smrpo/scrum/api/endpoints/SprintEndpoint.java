package si.smrpo.scrum.api.endpoints;


import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.api.endpoints.defs.SprintEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.lib.requests.AddStoryRequest;
import si.smrpo.scrum.lib.responses.SprintStatus;
import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.services.SprintService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.mjamsek.rest.Rest.HttpHeaders.X_TOTAL_COUNT;

@SecureResource
@Path("/sprints")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped

public class SprintEndpoint implements SprintEndpointDef {

    @Inject
    private QueryParameters queryParameters;
    
    @Inject
    private SprintService sprintService;
    
    @Override
    public Response getSprintById(String sprintId) {
        Sprint sprint = sprintService.getSprintById(sprintId);
        return Response.ok(sprint).build();
    }
    
    @Override
    public Response getSprintStories(String sprintId) {
        EntityList<Story> stories = sprintService.getSprintStories(sprintId, queryParameters);
        return Response.ok(stories.getEntities())
            .header(X_TOTAL_COUNT, stories.getCount())
            .build();
    }
    
    @Override
    public Response addStoriesToSprint(String sprintId, AddStoryRequest request) {
        sprintService.addStoriesToSprint(sprintId, request);
        return Response.noContent().build();
    }
    
    @Override
    public Response getSprintStatus(String sprintId) {
        SprintStatus status = sprintService.getSprintStatus(sprintId);
        return Response.ok(status).build();
    }
    
}
