package si.smrpo.scrum.persistence.docs;

import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.project.ProjectEntity;

import javax.persistence.*;

@Entity
@Table(name = "project_documentation_pages", indexes = {
    @Index(name = "INDEX_PROJECT_DOCUMENTATION_PROJECTS", columnList = "project_id")
})
@NamedQueries({
    @NamedQuery(name = ProjectDocumentationEntity.GET_BY_PROJECT, query = "SELECT d FROM ProjectDocumentationEntity d WHERE d.project.id = :projectId")
})
public class ProjectDocumentationEntity extends BaseEntity {
    
    public static final String GET_BY_PROJECT = "ProjectDocumentationEntity.getByProject";
    
    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;
    
    @Column(name = "md_content", columnDefinition = "TEXT")
    private String markdownContent;
    
    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;
    
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;
    
    public String getTextContent() {
        return textContent;
    }
    
    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }
    
    public String getMarkdownContent() {
        return markdownContent;
    }
    
    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }
    
    public String getHtmlContent() {
        return htmlContent;
    }
    
    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }
    
    public ProjectEntity getProject() {
        return project;
    }
    
    public void setProject(ProjectEntity project) {
        this.project = project;
    }
}
