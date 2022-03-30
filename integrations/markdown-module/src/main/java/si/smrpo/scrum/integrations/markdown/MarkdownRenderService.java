package si.smrpo.scrum.integrations.markdown;

public interface MarkdownRenderService {

    String convertMarkdownToHtml(String markdown);
    
    String convertMarkdownToText(String markdown);

}
