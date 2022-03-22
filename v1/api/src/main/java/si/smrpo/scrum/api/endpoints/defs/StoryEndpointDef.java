package si.smrpo.scrum.api.endpoints.defs;

import si.smrpo.scrum.lib.requests.CreateStoryRequest;
import si.smrpo.scrum.lib.stories.Story;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public interface StoryEndpointDef {



    @GET
    @Path("/{storyId}")
    Response getStoryById(@PathParam("storyId") String storyId);



}
