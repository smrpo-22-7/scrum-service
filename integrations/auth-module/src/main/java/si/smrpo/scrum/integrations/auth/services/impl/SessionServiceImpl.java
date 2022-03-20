package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import si.smrpo.scrum.integrations.auth.services.SessionService;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.enums.SessionStatus;
import si.smrpo.scrum.persistence.auth.SessionEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@RequestScoped
public class SessionServiceImpl implements SessionService {
    
    private static final Logger LOG = LogManager.getLogger(SessionServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Override
    public SessionEntity startSession(String ipAddress) {
        LOG.debug("Starting session...");
        SessionEntity session = new SessionEntity();
        session.setIpAddress(ipAddress);
        session.setStatus(SessionStatus.CREATED);
    
        try {
            em.getTransaction().begin();
            em.persist(session);
            em.getTransaction().commit();
            LOG.debug("Session persisted");
            return session;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public SessionEntity associateUserWithSession(String sessionId, String userId) {
        LOG.debug("Associating user with session...");
    
        try {
            em.getTransaction().begin();
    
            SessionEntity entity = getSessionById(sessionId)
                .orElseThrow(() -> new UnauthorizedException("Invalid session!"));
            
            getExistingUserSession(entity.getIpAddress(), userId)
                .ifPresent(existingSession -> {
                    if (!existingSession.getId().equals(entity.getId())) {
                        LOG.debug("Found existing stored session. Cleaning it up...");
                        em.remove(existingSession);
                        em.flush();
                    }
                });
    
            UserEntity user = userService.getUserEntityById(userId)
                .orElseThrow(() -> new UnauthorizedException("Invalid user!"));
            
            entity.setUser(user);
            em.flush();
            em.getTransaction().commit();
            LOG.debug("User associated with session...");
            return entity;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public SessionEntity activateSession(String sessionId) {
        LOG.debug("Associating user with session...");
    
        try {
            em.getTransaction().begin();
        
            SessionEntity entity = getSessionById(sessionId)
                .orElseThrow(() -> new UnauthorizedException("Invalid session!"));
        
            entity.setStatus(SessionStatus.ACTIVE);
            em.flush();
            em.getTransaction().commit();
            LOG.debug("User associated with session...");
            return entity;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private Optional<SessionEntity> getExistingUserSession(String ipAddress, String userId) {
        TypedQuery<SessionEntity> query = em.createNamedQuery(SessionEntity.GET_BY_USER_AND_IP, SessionEntity.class);
        query.setParameter("ip", ipAddress);
        query.setParameter("userId", userId);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<SessionEntity> getSession(String sessionId, String ipAddress) {
        TypedQuery<SessionEntity> query = em.createNamedQuery(SessionEntity.GET_SESSION, SessionEntity.class);
        query.setParameter("sessionId", sessionId);
        query.setParameter("ip", ipAddress);
    
        try {
            SessionEntity entity = query.getSingleResult();
            return Optional.of(entity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<SessionEntity> getSessionById(String sessionId) {
        return Optional.ofNullable(em.find(SessionEntity.class, sessionId));
    }
    
    @Override
    public void endSession(String sessionId) {
        LOG.debug("Ending session...");
        getSessionById(sessionId).ifPresent(session -> {
            try {
                em.getTransaction().begin();
                em.remove(session);
                em.getTransaction().commit();
                LOG.debug("Session removed");
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
            }
        });
    }
}
