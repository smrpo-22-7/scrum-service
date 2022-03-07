package si.smrpo.scrum.api.listeners;

import org.mindrot.jbcrypt.BCrypt;
import si.smrpo.scrum.integrations.auth.services.KeyService;
import si.smrpo.scrum.integrations.auth.services.SigningService;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
@ApplicationScoped
public class StartupListener implements ServletContextListener {
    
    @Inject
    private EntityManager em;
    
    @Inject
    private KeyService keyService;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        keyService.initializeKeys();
        keyService.loadKeysToRegistry();
        
        // Test
        UserEntity user = new UserEntity();
        user.setUsername("testko");
        user.setPassword(BCrypt.hashpw("geslo123", BCrypt.gensalt()));
        user.setFirstName("Testko");
        user.setLastName("Testkovic");
        user.setEmail("test@mail.com");
        
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    
    }
}
