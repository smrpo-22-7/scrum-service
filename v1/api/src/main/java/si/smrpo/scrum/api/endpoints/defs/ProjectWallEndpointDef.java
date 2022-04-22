package si.smrpo.scrum.api.endpoints.defs;

import com.mjamsek.rest.Rest;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import si.smrpo.scrum.lib.projects.ProjectWallPost;
import si.smrpo.scrum.lib.requests.ConflictCheckRequest;
import si.smrpo.scrum.lib.responses.ExtendedStory;
import si.smrpo.scrum.lib.stories.Story;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface ProjectWallEndpointDef {
    
    @GET
    @Path("/{projectId}/posts")
    @Tag(name = "project-wall")
    @Operation(summary = "get wall posts")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectWallPost.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getPosts(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/posts/{postId}")
    @Tag(name = "project-wall")
    @Operation(summary = "get wall post")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ExtendedStory.class)))
    })
    Response getPost(@PathParam("projectId") String projectId, @PathParam("postId") String postId);
    
    @POST
    @Path("/{projectId}/posts")
    @Tag(name = "project-wall")
    @Operation(summary = "save wall post")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ProjectWallPost.class)))
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response savePost(@PathParam("projectId") String projectId, ProjectWallPost post);
    
    @DELETE
    @Path("/{projectId}/posts/{postId}")
    @Tag(name = "project-wall")
    @Operation(summary = "remove wall post")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response removePost(@PathParam("projectId") String projectId, @PathParam("postId") String postId);
    
}
