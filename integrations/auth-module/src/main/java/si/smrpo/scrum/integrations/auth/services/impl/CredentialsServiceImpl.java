package si.smrpo.scrum.integrations.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import si.smrpo.scrum.integrations.auth.runnables.PasswordResetRunnable;
import si.smrpo.scrum.integrations.auth.services.CredentialsService;
import si.smrpo.scrum.integrations.auth.utils.KeyUtil;
import si.smrpo.scrum.persistence.auth.PasswordResetRequestEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RequestScoped
public class CredentialsServiceImpl implements CredentialsService {
    
    private static final Logger LOG = LogManager.getLogger(CredentialsServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private PasswordResetRunnable passwordResetRunnable;
    
    @Override
    public void sendResetPasswordMessage(String email, String ipAddress, Map<String, String[]> params) {
        CompletableFuture.runAsync(() -> {
            passwordResetRunnable.run(ipAddress, email, params);
        }).exceptionally(e -> {
            LOG.error("Error sending password reset!", e);
            return null;
        });
    }
    
    @Override
    public Optional<UserEntity> validateResetPasswordChallenge(String challenge, String ipAddress) {
        return getResetRequest(challenge).flatMap(requestEntity -> {
            String verifier = KeyUtil.sha256(createResetPasswordCode(requestEntity.getUser().getId(), ipAddress));
            Date now = new Date();
            if (now.after(requestEntity.getExpirationDate())) {
                return Optional.empty();
            }
            if (challenge.equals(verifier)) {
                removeRequest(requestEntity);
                return Optional.of(requestEntity.getUser());
            }
            return Optional.empty();
        });
    }
    
    private Optional<PasswordResetRequestEntity> getResetRequest(String challenge) {
        TypedQuery<PasswordResetRequestEntity> query = em.createNamedQuery(PasswordResetRequestEntity.GET_BY_CHALLENGE, PasswordResetRequestEntity.class);
        query.setParameter("challenge", challenge);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private void removeRequest(PasswordResetRequestEntity request) {
        try {
            em.getTransaction().begin();
            em.remove(request);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private String createResetPasswordCode(String userId, String ipAddress) {
        return userId + ":" + ipAddress;
    }
    
}
