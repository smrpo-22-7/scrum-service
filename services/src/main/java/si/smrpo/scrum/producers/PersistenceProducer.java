package si.smrpo.scrum.producers;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class PersistenceProducer {
    
    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;
    
    @Produces
    @Dependent
    public EntityManager getDefaultEntityManager() {
        return em;
    }
    
    public void close(@Disposes EntityManager em) {
        em.close();
    }
    
}
