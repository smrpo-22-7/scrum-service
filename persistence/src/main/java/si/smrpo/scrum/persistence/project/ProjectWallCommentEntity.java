package si.smrpo.scrum.persistence.project;

import si.smrpo.scrum.lib.enums.SimpleStatus;
import si.smrpo.scrum.persistence.BaseEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "project_wall_comments", indexes = {
    @Index(name = "INDEX_PROJECT_WALL_POSTS", columnList = "post_id")
})
public class ProjectWallCommentEntity extends BaseEntity {
    
    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;
    
    @Column(name = "md_content", columnDefinition = "TEXT")
    private String markdownContent;
    
    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SimpleStatus status;
    
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;
    
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private ProjectWallPostEntity post;
    
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
    
    public UserEntity getAuthor() {
        return author;
    }
    
    public void setAuthor(UserEntity author) {
        this.author = author;
    }
    
    public ProjectWallPostEntity getPost() {
        return post;
    }
    
    public void setPost(ProjectWallPostEntity post) {
        this.post = post;
    }
}
