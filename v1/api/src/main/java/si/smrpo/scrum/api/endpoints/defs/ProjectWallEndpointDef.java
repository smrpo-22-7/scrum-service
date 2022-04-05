package si.smrpo.scrum.api.endpoints.defs;

import si.smrpo.scrum.lib.projects.ProjectWallPost;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

public interface ProjectWallEndpointDef {
    
    @GET
    @Path("/{projectId}/posts")
    Response getPosts(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/posts/{postId}")
    Response getPost(@PathParam("projectId") String projectId, @PathParam("postId") String postId);
    
    @POST
    @Path("/{projectId}/posts")
    Response savePost(@PathParam("projectId") String projectId, ProjectWallPost post);
    
    @DELETE
    @Path("/{projectId}/posts/{postId}")
    Response removePost(@PathParam("projectId") String projectId, @PathParam("postId") String postId);
    
}
