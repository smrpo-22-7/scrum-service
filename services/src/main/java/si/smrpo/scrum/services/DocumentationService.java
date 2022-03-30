package si.smrpo.scrum.services;

import si.smrpo.scrum.lib.ProjectDocumentation;
import si.smrpo.scrum.lib.responses.DocumentationContentResponse;
import si.smrpo.scrum.persistence.docs.ProjectDocumentationEntity;

import java.io.InputStream;
import java.util.Optional;

public interface DocumentationService {
    
    void saveDocumentation(String projectId, ProjectDocumentation documentation);
    
    void saveDocumentation(String projectId, InputStream markdownInputStream);
    
    ProjectDocumentation getDocumentation(String projectId);
    
    Optional<ProjectDocumentationEntity> getDocumentationEntity(String projectId);
    
    DocumentationContentResponse getDocumentationContentBytes(String projectId);
    
    DocumentationContentResponse getDocumentationContentHtml(String projectId);
    
}
