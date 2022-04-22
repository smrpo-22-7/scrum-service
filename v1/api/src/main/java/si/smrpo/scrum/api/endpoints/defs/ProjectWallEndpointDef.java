package si.smrpo.scrum.api.endpoints.defs;

import com.kumuluz.ee.rest.enums.OrderDirection;
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
import si.smrpo.scrum.api.params.ProjectWallPostsParams;
import si.smrpo.scrum.lib.projects.ProjectWallComment;
import si.smrpo.scrum.lib.projects.ProjectWallPost;
import si.smrpo.scrum.lib.responses.ExtendedStory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface ProjectWallEndpointDef {
    
    @GET
    @Path("/{projectId}/posts")
    @Tag(name = "project-wall")
    @Operation(summary = "get wall posts")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @Parameter(name = "sort", in = ParameterIn.QUERY, schema = @Schema(implementation = OrderDirection.class))
    @Parameter(name = "limit", in = ParameterIn.QUERY, schema = @Schema(implementation = Long.class))
    @Parameter(name = "offset", in = ParameterIn.QUERY, schema = @Schema(implementation = Long.class))
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectWallPost.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getPosts(@PathParam("projectId") String projectId, @BeanParam ProjectWallPostsParams params);
    
    @GET
    @Path("/posts/{postId}/comments")
    @Tag(name = "project-wall")
    @Operation(summary = "get post's comments")
    @Parameter(name = "postId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ProjectWallComment.class, type = SchemaType.ARRAY)), headers = {
            @Header(name = Rest.HttpHeaders.X_TOTAL_COUNT, schema = @Schema(type = SchemaType.INTEGER))
        })
    })
    Response getPostComments(@PathParam("postId") String postId);
    
    @GET
    @Path("/posts/{postId}")
    @Tag(name = "project-wall")
    @Operation(summary = "get wall post")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content =
        @Content(mediaType = MediaType.APPLICATION_JSON, schema =
        @Schema(implementation = ExtendedStory.class)))
    })
    Response getPost(@PathParam("postId") String postId);
    
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
    
    @POST
    @Path("/posts/{postId}/comments")
    @Tag(name = "project-wall")
    @Operation(summary = "save wall post's comment")
    @Parameter(name = "postId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ProjectWallComment.class)))
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response addComment(@PathParam("postId") String postId, ProjectWallComment comment);
    
    @DELETE
    @Path("/posts/{postId}")
    @Tag(name = "project-wall")
    @Operation(summary = "remove wall post")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response removePost(@PathParam("postId") String postId);
    
    @DELETE
    @Path("/posts/comments/{commentId}")
    @Tag(name = "project-wall")
    @Operation(summary = "remove wall post")
    @Parameter(name = "commentId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response removeComment(@PathParam("commentId") String commentId);
    
}
