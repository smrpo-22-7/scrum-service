package si.smrpo.scrum.integrations.html.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import si.smrpo.scrum.integrations.html.HtmlProcessingService;

import javax.enterprise.context.RequestScoped;
import java.util.Locale;

@RequestScoped
public class HtmlProcessingServiceImpl implements HtmlProcessingService {
    
    private static final Logger LOG = LogManager.getLogger(HtmlProcessingServiceImpl.class.getName());
    
    @Override
    public String linkifyTitles(String html) {
        try {
            Document document = Jsoup.parse(html);
            Elements titleElements = document.select("h1,h2,h3");
            for(Element elem : titleElements) {
                String titleContent = elem.text();
                String handle = toHandle(titleContent);
                
                String htmlContent = String.format("<a class=\"title-anchor\" href=\"%s\"></a>" + titleContent, "#" + handle);
                elem.id(handle).html(htmlContent);
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
            return Jsoup.clean(html, Safelist.basic());
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
