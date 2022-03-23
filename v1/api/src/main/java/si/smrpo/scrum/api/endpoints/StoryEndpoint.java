package si.smrpo.scrum.api.endpoints;

import si.smrpo.scrum.api.endpoints.defs.StoryEndpointDef;
import si.smrpo.scrum.integrations.auth.Roles;
import si.smrpo.scrum.integrations.auth.models.annotations.SecureResource;
import si.smrpo.scrum.integrations.auth.models.annotations.SysRolesRequired;
import si.smrpo.scrum.lib.stories.Story;
import si.smrpo.scrum.services.StoryService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SecureResource
@Path("/stories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped

public class StoryEndpoint implements StoryEndpointDef {

    @Inject
    private StoryService storyService;

    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response getStoryById(String storyId) {
        Story story = storyService.getStoryById(storyId);
        return Response.ok(story).build();
    }
    
    @SysRolesRequired({Roles.USER_ROLE})
    @Override
    public Response updateTimeEstimate(String storyId, Story story) {
        Story updatedStory = storyService.updateTimeEstimate(storyId, story);
        return Response.ok(updatedStory).build();
    }
    
}
