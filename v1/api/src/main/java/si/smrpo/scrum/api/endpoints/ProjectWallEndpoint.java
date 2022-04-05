package si.smrpo.scrum.api.endpoints;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.rest.Rest;
import com.mjamsek.rest.dto.EntityList;
import si.smrpo.scrum.api.endpoints.defs.ProjectWallEndpointDef;
import si.smrpo.scrum.lib.projects.ProjectWallPost;
import si.smrpo.scrum.services.ProjectWallService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectWallEndpoint implements ProjectWallEndpointDef {
    
    @Inject
    private ProjectWallService projectWallService;
    
    @Inject
    private QueryParameters queryParameters;
    
    @Override
    public Response getPosts(String projectId) {
        EntityList<ProjectWallPost> posts = projectWallService.getPosts(projectId, queryParameters);
        return Response.ok(posts.getEntities())
            .header(Rest.HttpHeaders.X_TOTAL_COUNT, posts.getCount())
            .build();
    }
    
    @Override
    public Response getPost(String projectId, String postId) {
        return Response.ok(projectWallService.getPost(postId)).build();
    }
    
    @Override
    public Response savePost(String projectId, ProjectWallPost post) {
        projectWallService.addPost(projectId, post);
        return Response.noContent().build();
    }
    
    @Override
    public Response removePost(String projectId, String postId) {
        projectWallService.removePost(postId);
        return Response.noContent().build();
    }
}
