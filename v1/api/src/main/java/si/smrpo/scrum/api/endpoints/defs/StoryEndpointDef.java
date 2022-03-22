package si.smrpo.scrum.api.endpoints.defs;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public interface StoryEndpointDef {

    @GET
    @Path("/{storyId}")
    @Tag(name = "stories")
    Response getStoryById(@PathParam("storyId") String storyId);
    
}
