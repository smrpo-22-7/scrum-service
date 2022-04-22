package si.smrpo.scrum.lib.projects;

import si.smrpo.scrum.lib.BaseType;
import si.smrpo.scrum.lib.UserProfile;
import si.smrpo.scrum.lib.enums.SimpleStatus;

public class ProjectWallComment extends BaseType {
    
    private String textContent;
    
    private String markdownContent;
    
    private String htmlContent;
    
    private SimpleStatus status;
    
    private UserProfile author;
    
    private String authorId;
    
    private ProjectWallPost post;
    
    private String postId;
    
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
    
    public SimpleStatus getStatus() {
        return status;
    }
    
    public void setStatus(SimpleStatus status) {
        this.status = status;
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
    
    public ProjectWallPost getPost() {
        return post;
    }
    
    public void setPost(ProjectWallPost post) {
        this.post = post;
    }
    
    public String getPostId() {
        return postId;
    }
    
    public void setPostId(String postId) {
        this.postId = postId;
    }
}
