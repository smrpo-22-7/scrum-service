package si.smrpo.scrum.api.endpoints.defs;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import si.smrpo.scrum.lib.ProjectDocumentation;
import si.smrpo.scrum.lib.meta.FileUploadRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface DocumentationEndpointDef {
    
    @GET
    @Path("/{projectId}/documentation")
    @Tag(name = "docs")
    @Operation(summary = "get documentation data")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content = @Content(
            mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProjectDocumentation.class)
        ))
    })
    Response getDocumentation(@PathParam("projectId") String projectId);
    
    @GET
    @Path("/{projectId}/documentation/content")
    @Produces("text/markdown")
    @Tag(name = "docs")
    @Operation(summary = "get documentation content (markdown)")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content = @Content(mediaType = "text/markdown",
            schema = @Schema(type = SchemaType.STRING, format = "binary")))
    })
    Response getDocumentationContentMarkdown(@PathParam("projectId") String projectId,
                                             @QueryParam("attachment") @DefaultValue("false") boolean asAttachment);
    
    @GET
    @Path("/{projectId}/documentation/content")
    @Produces(MediaType.TEXT_HTML)
    @Tag(name = "docs")
    @Operation(summary = "get documentation content (html)")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @APIResponses({
        @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.TEXT_HTML,
            schema = @Schema(type = SchemaType.STRING)))
    })
    Response getDocumentationContentHtml(@PathParam("projectId") String projectId,
                                         @QueryParam("attachment") @DefaultValue("false") boolean asAttachment);
    
    @PUT
    @Path("/{projectId}/documentation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Tag(name = "docs")
    @Operation(summary = "set documentation content")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = ProjectDocumentation.class)))
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response saveDocumentation(@PathParam("projectId") String projectId, ProjectDocumentation documentation);
    
    @PUT
    @Path("/{projectId}/documentation")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Tag(name = "docs")
    @Operation(summary = "set documentation content")
    @Parameter(name = "projectId", in = ParameterIn.PATH, required = true)
    @RequestBody(required = true, content = @Content(
        mediaType = MediaType.MULTIPART_FORM_DATA,
        schema = @Schema(type = SchemaType.OBJECT, implementation = FileUploadRequest.class)
    ))
    @APIResponses({
        @APIResponse(responseCode = "204")
    })
    Response saveDocumentation(@PathParam("projectId") String projectId, @FormDataParam("file") FormDataBodyPart body);
    
}
