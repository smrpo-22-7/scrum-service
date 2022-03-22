package si.smrpo.scrum.api.endpoints;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.api.endpoints.defs.SprintEndpointDef;
import si.smrpo.scrum.api.endpoints.defs.StoryEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.sprints.Sprint;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.services.SprintService;
import si.smrpo.scrum.services.StoryService;

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

public class StoryEndpoint implements StoryEndpointDef {

    @Inject
    private QueryParameters queryParameters;

    @Inject
    private StoryService storyService;

    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response getStoryById(String storyId) {
        Story story = storyService.getStoryById(storyId);
        return Response.ok(story).build();
    }

}
