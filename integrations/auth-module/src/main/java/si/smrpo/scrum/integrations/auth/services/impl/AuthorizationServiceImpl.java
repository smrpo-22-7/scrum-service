package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.utils.DatetimeUtil;
import si.smrpo.scrum.integrations.auth.services.AuthorizationService;
import si.smrpo.scrum.integrations.auth.services.SessionService;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.lib.enums.PKCEMethod;
import si.smrpo.scrum.persistence.auth.AuthorizationRequestEntity;
import si.smrpo.scrum.persistence.auth.SessionEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.ws.rs.BadRequestException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class AuthorizationServiceImpl implements AuthorizationService {
    
    private static final Logger LOG = LogManager.getLogger(AuthorizationServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Inject
    private SessionService sessionService;
    
    @Override
    public AuthorizationRequestEntity initializeRequest(String userIp, String pkceChallenge, PKCEMethod pkceMethod) throws BadRequestException {
        LOG.debug("Initializing authorization request...");
        try {
            em.getTransaction().begin();
            
            getRequestEntityByIp(userIp)
                .ifPresent(request -> {
                    LOG.debug("Cleaning up previous stale authorization requests");
                    em.remove(request);
                    em.flush();
                });
            
            AuthorizationRequestEntity request = new AuthorizationRequestEntity();
            request.setIpAddress(userIp);
            
            // If client has defined PKCE method, PKCE params must be present
            validateAndSetPKCE(request, pkceChallenge, pkceMethod);
            em.persist(request);
            em.getTransaction().commit();
            LOG.debug("Authorization request is persisted");
            return request;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public AuthorizationRequestEntity createAuthorizationCode(String requestId, String userId) {
        LOG.debug("Creating authorization code...");
        AuthorizationRequestEntity request = getRequestEntityById(requestId)
            .orElseThrow(() -> {
                LOG.debug("Request with given id not found!");
                return new UnauthorizedException("error.unauthorized");
            });
        
        UserEntity user = userService.getUserEntityById(userId)
            .orElseThrow(() -> {
                LOG.debug("User with given id not found!");
                return new UnauthorizedException("error.unauthorized");
            });
        
        final int codeValidTime = 5;
        try {
            em.getTransaction().begin();
            request.setCode(UUID.randomUUID().toString());
            request.setCodeExpiration(DatetimeUtil.getMinutesAfterNow(codeValidTime));
            request.setUser(user);
            em.getTransaction().commit();
            LOG.debug("Authorization code persisted");
            return request;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public AuthorizationRequestEntity initializeSessionRequest(String sessionId, String ipAddress, String pkceChallenge, PKCEMethod pkceMethod) {
        LOG.debug("Initializing session scoped authorization request...");
        SessionEntity session = sessionService.getSession(sessionId, ipAddress)
            .orElseThrow(() -> {
                LOG.debug("Invalid session given!");
                return new UnauthorizedException("error.unauthorized");
            });
        
        final int codeValidTime = 5;
        
        try {
            em.getTransaction().begin();
            
            getRequestEntityByIp(session.getIpAddress())
                .ifPresent(request -> {
                    LOG.debug("Cleaning up previous stale authorization requests");
                    em.remove(request);
                    em.flush();
                });
            
            AuthorizationRequestEntity request = new AuthorizationRequestEntity();
            request.setIpAddress(session.getIpAddress());
            request.setCode(UUID.randomUUID().toString());
            request.setCodeExpiration(DatetimeUtil.getMinutesAfterNow(codeValidTime));
            request.setUser(session.getUser());
            
            // If client has defined PKCE method, PKCE params must be present
            validateAndSetPKCE(request, pkceChallenge, pkceMethod);
            em.persist(request);
            LOG.debug("Session-scoped authorization request is persisted.");
            em.getTransaction().commit();
            return request;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private void validateAndSetPKCE(AuthorizationRequestEntity request, String pkceChallenge, PKCEMethod pkceMethod) {
        if (pkceChallenge == null || pkceMethod == null) {
            LOG.debug("PKCE challenge and/or method are null!");
            throw new UnauthorizedException("error.unauthorized");
        }
        if (!pkceMethod.equals(PKCEMethod.S256)) {
            LOG.debug("Invalid PKCE method! Only S256 is supported.");
            throw new UnauthorizedException("error.unauthorized");
        }
        request.setPkceChallenge(pkceChallenge);
        request.setPkceMethod(pkceMethod);
    }
    
    @Override
    public AuthorizationRequestEntity recordPKCEChallenge(String requestId, String pkceChallenge, PKCEMethod pkceMethod) {
        LOG.debug("Recording PKCE challenge...");
        AuthorizationRequestEntity request = getRequestEntityById(requestId)
            .orElseThrow(() -> {
                LOG.debug("Request with given id not found!");
                return new UnauthorizedException("error.unauthorized");
            });
        try {
            em.getTransaction().begin();
            request.setPkceChallenge(pkceChallenge);
            request.setPkceMethod(pkceMethod);
            em.getTransaction().commit();
            LOG.debug("PKCE challenge persisted");
            return request;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("");
        }
    }
    
    @Override
    public void removeAuthorizationRequest(String requestId) {
        LOG.debug("Removing authorization request...");
        getRequestEntityById(requestId).ifPresent(request -> {
            try {
                em.getTransaction().begin();
                em.remove(request);
                em.getTransaction().commit();
                LOG.debug("Authorization request removed");
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("error.server");
            }
        });
    }
    
    @Override
    public Optional<AuthorizationRequestEntity> getRequestByCode(String code) {
        TypedQuery<AuthorizationRequestEntity> query = em.createNamedQuery(AuthorizationRequestEntity.GET_BY_CODE, AuthorizationRequestEntity.class);
        query.setParameter("code", code);
        query.setParameter("nowDate", new Date());
    
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
    public Optional<AuthorizationRequestEntity> getRequestEntityByIp(String ipAddress) {
        TypedQuery<AuthorizationRequestEntity> query = em.createNamedQuery(AuthorizationRequestEntity.GET_BY_IP, AuthorizationRequestEntity.class);
        query.setParameter("ipAddress", ipAddress);
    
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<AuthorizationRequestEntity> getRequestEntityById(String requestId) {
        return Optional.ofNullable(em.find(AuthorizationRequestEntity.class, requestId));
    }
}
