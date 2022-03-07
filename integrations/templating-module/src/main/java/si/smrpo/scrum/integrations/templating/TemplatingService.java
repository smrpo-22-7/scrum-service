package si.smrpo.scrum.integrations.templating;

import java.util.Map;

public interface TemplatingService {
    
    String renderHtml(String htmlTemplate, Map<String, Object> params);
    
}
