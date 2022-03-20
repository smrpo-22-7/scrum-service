package si.smrpo.scrum.integrations.auth.runnables;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.utils.DatetimeUtil;
import si.smrpo.scrum.integrations.auth.config.AuthConfig;
import si.smrpo.scrum.integrations.auth.services.UserService;
import si.smrpo.scrum.integrations.auth.utils.HttpUtil;
import si.smrpo.scrum.integrations.auth.utils.KeyUtil;
import si.smrpo.scrum.integrations.messaging.Message;
import si.smrpo.scrum.integrations.messaging.MessagingService;
import si.smrpo.scrum.integrations.messaging.email.EmailMessageBuilder;
import si.smrpo.scrum.integrations.templating.TemplatingService;
import si.smrpo.scrum.persistence.auth.PasswordResetRequestEntity;
import si.smrpo.scrum.persistence.users.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static si.smrpo.scrum.integrations.auth.AuthConstants.CHALLENGE_RESET_PASSWORD_PARAM;
import static si.smrpo.scrum.integrations.auth.ServletConstants.RESET_PASSWORD_SERVLET;

@ApplicationScoped
public class PasswordResetRunnable {
    
    private static final Logger LOG = LogManager.getLogger(PasswordResetRunnable.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Inject
    private TemplatingService templatingService;
    
    @Inject
    private MessagingService messagingService;
    
    @Inject
    private AuthConfig authConfig;
    
    @ActivateRequestContext
    public void run(String ipAddress, String email, Map<String, String[]> params) {
        LOG.trace("Sending reset password email...");
        Optional<UserEntity> userOpt = userService.getUserEntityByEmail(email);
        LOG.debug("Email is valid, sending email...");
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            String resetCode = KeyUtil.sha256(createResetPasswordCode(user.getId(), ipAddress));
            String resetUrl = createPasswordResetUrl(resetCode, params);
        
            try {
                em.getTransaction().begin();
                
                getResetRequest(resetCode).ifPresent(requestEntity -> {
                    em.remove(requestEntity);
                    em.flush();
                });
    
                PasswordResetRequestEntity entity = new PasswordResetRequestEntity();
                entity.setIpAddress(ipAddress);
                entity.setUser(user);
                entity.setChallenge(resetCode);
                entity.setExpirationDate(DatetimeUtil.getMinutesAfterNow(10));
    
                em.persist(entity);
                em.getTransaction().commit();
    
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("resetUrl", resetUrl);
                String htmlContent = templatingService.renderHtml("password-email", templateParams);
                Message emailMessage = EmailMessageBuilder.newBuilder()
                    .subject("Reset password")
                    .recipient(user.getEmail())
                    .content(htmlContent)
                    .build();
    
                messagingService.sendMessage(emailMessage);
                LOG.debug("Sent password reset message!");
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("error.server");
            }
        }
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
    
    private String createResetPasswordCode(String userId, String ipAddress) {
        return userId + ":" + ipAddress;
    }
    
    
    private String createPasswordResetUrl(String code, Map<String, String[]> params) {
        Map<String, String[]> urlParams = new HashMap<>(params);
        urlParams.put(CHALLENGE_RESET_PASSWORD_PARAM, new String[]{code});
        return this.authConfig.getBaseUrl() + RESET_PASSWORD_SERVLET
            + HttpUtil.formatQueryParams(urlParams);
    }
}
