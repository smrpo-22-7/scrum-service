package si.smrpo.scrum.integrations.templating;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@ApplicationScoped
public class TemplatingServiceImpl implements TemplatingService {
    
    private TemplateEngine templateEngine;
    
    @PostConstruct
    private void init() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/html/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setOrder(1);
        
        boolean cacheTemplates = ConfigurationUtil.getInstance().getBoolean("templates.cache-enabled").orElse(true);
        templateResolver.setCacheable(cacheTemplates);
        
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);
        this.templateEngine = engine;
    }
    
    @Override
    public String renderHtml(String htmlTemplate, Map<String, Object> params) {
        Context context = new Context();
        params.keySet().forEach(key -> context.setVariable(key, params.get(key)));
        try {
            return this.templateEngine.process(htmlTemplate, context);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error generating template!");
        }
    }
}
