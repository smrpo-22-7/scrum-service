package si.smrpo.scrum.mappers;

import si.smrpo.scrum.lib.ProjectDocumentation;
import si.smrpo.scrum.persistence.docs.ProjectDocumentationEntity;

public class DocumentationMapper {
    
    public static ProjectDocumentation base(ProjectDocumentationEntity entity, boolean includeMarkdown, boolean includeHtml, boolean includeText) {
        ProjectDocumentation docs = BaseMapper.fromEntity(entity, ProjectDocumentation.class);
        if (entity.getProject() != null) {
            docs.setProjectId(entity.getProject().getId());
        }
        if (includeHtml) {
            docs.setHtmlContent(entity.getHtmlContent());
        }
        if (includeMarkdown) {
            docs.setMarkdownContent(entity.getMarkdownContent());
        }
        if (includeText) {
            docs.setTextContent(entity.getTextContent());
        }
        return docs;
    }
    
}
