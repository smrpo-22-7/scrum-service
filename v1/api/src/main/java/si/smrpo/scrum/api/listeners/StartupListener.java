package si.smrpo.scrum.api.listeners;

import si.smrpo.scrum.integrations.auth.services.KeyService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
@ApplicationScoped
public class StartupListener implements ServletContextListener {
    
    @Inject
    private KeyService keyService;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        keyService.initializeKeys();
        keyService.loadKeysToRegistry();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    
    }
}
