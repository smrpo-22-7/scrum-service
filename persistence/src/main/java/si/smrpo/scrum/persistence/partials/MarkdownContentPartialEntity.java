package si.smrpo.scrum.persistence.partials;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable()
public class MarkdownContentPartialEntity implements Serializable {
    
    @Column(name = "text_content", columnDefinition = "TEXT")
    protected String textContent;
    
    @Column(name = "md_content", columnDefinition = "TEXT")
    protected String markdownContent;
    
    @Column(name = "html_content", columnDefinition = "TEXT")
    protected String htmlContent;
    
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
}
