package si.smrpo.scrum.api.endpoints;


import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.api.endpoints.defs.SprintEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.lib.sprints.Sprint;
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

    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response getSprintsList(String projectId) {
        EntityList<Sprint> sprints = sprintService.getSprints(queryParameters);
        return Response.ok(sprints.getEntities())
                .header(X_TOTAL_COUNT, sprints.getCount()).build();
    }

    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response getSprintById(String sprintId) {
        Sprint sprint = sprintService.getSprintById(sprintId);
        return Response.ok(sprint).build();
    }

    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response createSprint(String projectId, Sprint sprint) {
        Sprint createdSprint = sprintService.createSprint(sprint);
        return Response.status(Response.Status.CREATED).entity(createdSprint).build();
    }
}
