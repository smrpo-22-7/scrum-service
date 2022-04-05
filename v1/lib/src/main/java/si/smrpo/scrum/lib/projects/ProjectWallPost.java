package si.smrpo.scrum.lib.projects;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.enums.SimpleStatus;

public class ProjectWallPost extends BaseType {
    
    private String textContent;
    
    private String markdownContent;
    
    private String htmlContent;
    
    private SimpleStatus status;
    
    private UserProfile author;
    
    private String authorId;
    
    private String projectId;
    
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
    
    public UserProfile getAuthor() {
        return author;
    }
    
    public void setAuthor(UserProfile author) {
        this.author = author;
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public SimpleStatus getStatus() {
        return status;
    }
    
    public void setStatus(SimpleStatus status) {
        this.status = status;
    }
}
