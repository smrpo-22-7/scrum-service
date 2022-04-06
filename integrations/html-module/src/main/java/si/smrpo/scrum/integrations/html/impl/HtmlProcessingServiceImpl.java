package si.smrpo.scrum.integrations.html.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import si.smrpo.scrum.integrations.html.HtmlProcessingService;

import javax.enterprise.context.RequestScoped;
import java.util.Locale;

@RequestScoped
public class HtmlProcessingServiceImpl implements HtmlProcessingService {
    
    private static final Logger LOG = LogManager.getLogger(HtmlProcessingServiceImpl.class.getName());
    
    @Override
    public String linkifyTitles(String html, String linkTemplate) {
        
        try {
            Document document = Jsoup.parse(html);
            Elements titleElements = document.select("h1,h2,h3");
            for (Element elem : titleElements) {
                String titleContent = elem.text();
                String handle = toHandle(titleContent);
                
                Element anchor = new Element(Tag.valueOf("a"), "")
                    .addClass("title-anchor")
                    .attr("href", String.format(linkTemplate, handle));
                
                Element image = new Element(Tag.valueOf("img"), "")
                    .attr("src", "/assets/images/link.svg");
                anchor.appendChild(image);
                
                elem.html("")
                    .appendChild(anchor)
                    .appendText(titleContent)
                    .id(handle);
            }
            return document.toString();
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    }
    
    @Override
    public String sanitizeHtml(String html) {
        try {
            return Jsoup.clean(html, Safelist.relaxed());
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    }
    
    private String toHandle(String value) {
        value = value.toLowerCase(Locale.ROOT).trim();
        value = value.replaceAll(" +", " ");
        value = value.replaceAll("č", "c");
        value = value.replaceAll("š", "s");
        value = value.replaceAll("ž", "z");
        value = value.replaceAll("\\s", "-");
        return value.replaceAll("/[^a-z0-9-_]/", "");
    }
}
