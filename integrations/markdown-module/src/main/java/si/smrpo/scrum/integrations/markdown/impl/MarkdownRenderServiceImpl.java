package si.smrpo.scrum.integrations.markdown.impl;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentRenderer;
import si.smrpo.scrum.integrations.markdown.MarkdownRenderService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class MarkdownRenderServiceImpl implements MarkdownRenderService {
    
    private Parser parser;
    
    private HtmlRenderer htmlRenderer;
    
    private TextContentRenderer textRenderer;
    
    @PostConstruct
    private void init() {
        parser = Parser.builder().build();
        htmlRenderer = HtmlRenderer.builder().build();
        textRenderer = TextContentRenderer.builder().build();
    }
    
    @Override
    public String convertMarkdownToHtml(String markdown) {
        Node document = parser.parse(markdown);
        return htmlRenderer.render(document);
    }
    
    @Override
    public String convertMarkdownToText(String markdown) {
        Node document = parser.parse(markdown);
        return textRenderer.render(document);
    }
}
