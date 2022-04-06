package si.smrpo.scrum.integrations.html;

public interface HtmlProcessingService {
    
    String linkifyTitles(String html, String linkTemplate);
    
    String sanitizeHtml(String html);
    
}
