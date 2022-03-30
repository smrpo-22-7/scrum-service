package si.smrpo.scrum.api.endpoints;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import si.smrpo.scrum.api.endpoints.defs.DocumentationEndpointDef;
import si.smrpo.scrum.lib.ProjectDocumentation;
import si.smrpo.scrum.services.DocumentationService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@RequestScoped
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentationEndpoint implements DocumentationEndpointDef {
    
    @Inject
    private DocumentationService docsService;
    
    @Override
    public Response getDocumentation(String projectId) {
        ProjectDocumentation docs = docsService.getDocumentation(projectId);
        return Response.ok(docs).build();
    }
    
    @Override
    public Response getDocumentationContentMarkdown(String projectId, boolean asAttachment) {
        var resp = docsService.getDocumentationContentBytes(projectId);
        String contentDisposition = (asAttachment ? "attachment;" : "") + "filename=" + resp.getFilename();
        return Response.ok(resp.getBytes())
            .type("text/markdown")
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .build();
    }
    
    @Override
    public Response getDocumentationContentHtml(String projectId, boolean asAttachment) {
        var resp = docsService.getDocumentationContentHtml(projectId);
        String contentDisposition = (asAttachment ? "attachment;" : "") + "filename=" + resp.getFilename();
        return Response.ok(resp.getBytes())
            .type(MediaType.TEXT_HTML)
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .build();
    }
    
    @Override
    public Response saveDocumentation(String projectId, ProjectDocumentation documentation) {
        docsService.saveDocumentation(projectId, documentation);
        return Response.noContent().build();
    }
    
    @Override
    public Response saveDocumentation(String projectId, FormDataBodyPart body) {
        InputStream fileInputStream = body.getEntityAs(InputStream.class);
        docsService.saveDocumentation(projectId, fileInputStream);
        return Response.noContent().build();
    }
}
