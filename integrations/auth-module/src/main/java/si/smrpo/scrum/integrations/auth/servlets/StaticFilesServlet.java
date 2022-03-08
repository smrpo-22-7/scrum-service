package si.smrpo.scrum.integrations.auth.servlets;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.util.resource.Resource;

import javax.servlet.UnavailableException;

public class StaticFilesServlet extends DefaultServlet {
    
    private static final Logger LOG = LogManager.getLogger(StaticFilesServlet.class.getName());
    
    private static final String STATIC_PREFIX = "/static";
    
    @Override
    public void init() throws UnavailableException {
        LOG.info("Mapping static resources at /static...");
        super.init();
    }
    
    @Override
    public Resource getResource(String pathInContext) {
        if (pathInContext.startsWith(STATIC_PREFIX)) {
            pathInContext = pathInContext.substring(STATIC_PREFIX.length());
        }
        return super.getResource(pathInContext);
    }
}
